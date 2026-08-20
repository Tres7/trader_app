# CD : déploiement VPS via SSH + Caddy/sslip.io

## Contexte

Le pipeline CI (build+push GHCR, manifest versionné, nightly Trivy/npm-audit) est opérationnel depuis longtemps, mais rien ne déploie réellement sur le VPS : `deploy.yml` n'existe pas, `backup-db.sh` est écrit et committé mais n'est appelé nulle part, et il n'y a ni reverse proxy ni HTTPS. Objectif : construire le chaînon manquant — SSH vers le VPS, Caddy (HTTPS auto via sslip.io, pas de nom de domaine acheté), déploiement piloté par les manifests existants, backup obligatoire avant chaque déploiement + nightly indépendant.

Décisions déjà actées avec l'utilisateur (ne pas rouvrir) :
- **Rollback manuel uniquement** — pas d'auto-rollback sur échec de health-check. Raisons données par l'utilisateur : un health-check mal calibré peut déclencher des rollbacks en boucle ou des faux positifs (cold start lent) ; Flyway ne rollback jamais le schéma DB, donc revenir à une ancienne image après une migration peut casser l'appli plus qu'avant ; en solo, un rollback silencieux automatique est dangereux (pas de visibilité). → `deploy.yml` prend un input `manifest_version` (vide = dernier, sinon = rollback manuel en relançant le workflow avec une version antérieure).
- **Alertes Discord/Telegram** : explicitement hors scope, reportées à plus tard.
- **Secrets** : uniquement sur le VPS (fichier `shared/server.env`, créé une fois à la main, jamais committé, jamais régénéré par la CI). Les secrets GitHub Actions se limitent à l'accès SSH (host/user/clé/port).
- **VPS déjà provisionné et joignable en SSH**, mais Caddy pas encore en place dessus.
- **Package GHCR reste privé** (revu après relecture — voir gap 4 ci-dessous) : authentification via `docker login` fait une fois à la main sur le VPS, pas de passage en public.

## Ce qui existe déjà et qu'il ne faut pas refaire

- `.github/scripts/backup-db.sh` — complet (pg_dump + upload R2 via rclone + rétention). À **appeler**, pas à réécrire. Il tourne forcément **sur le VPS** (fait du `docker exec`), jamais depuis un runner GitHub Actions.
- `.github/scripts/update-manifest.sh` et le format `deploy/manifests/manifest-X.Y.Z.yaml` (`schemaVersion`, `manifestVersion`, `services.server.{version,sourceRevision,image}` avec image pinnée par digest).
- Pattern de résolution du dernier manifest, déjà utilisé dans `nightly.yml` : `find deploy/manifests -maxdepth 1 -name 'manifest-*.yaml' | sed -E 's/.*manifest-([0-9]+\.[0-9]+\.[0-9]+)\.yaml/\1/' | sort -V | tail -1`.
- Convention shell des scripts ops (`backup-db.sh`) : `sh` POSIX + `set -eu` + helper `require_env` fail-fast — à reproduire pour tout nouveau script.
- Job `compose-config` dans `.github/workflows/backend-checks.yml` (`docker compose -f compose.yaml config --quiet`) — pattern à dupliquer pour valider le futur compose de prod en CI.

## Gaps découverts qu'il faut combler (sinon le déploiement ne peut pas marcher, ou marche mal)

1. **Aucun endpoint de health-check** : `server/build.gradle` n'a pas `spring-boot-starter-actuator`. À ajouter.
2. **Spring Security bloquerait le health-check** : `SecurityConfig.java` a `.anyRequest().authenticated()` — sans `permitAll()` explicite sur `/actuator/health`, le `wget` du healthcheck Docker reçoit un 401 et `--wait` ne se termine jamais.
3. **`server/.env.example` incomplet** : il manque `JWT_SECRET`, `MAIL_USERNAME/PASSWORD`, `SPRING_RABBITMQ_*`, `SPRING_PROFILES_ACTIVE`, et surtout `RABBITMQ_USER/PASS` — une paire de variables *différente* de `SPRING_RABBITMQ_USERNAME/PASSWORD` mais qui doit contenir la même valeur (sinon l'appli ne peut pas s'authentifier auprès de son propre broker). À corriger, avec un commentaire explicite sur ce piège.
4. **Visibilité du package GHCR** : `ghcr.io/tres7/trader_app/server` — package privé par défaut, donc le VPS ne peut pas `pull` l'image sans authentification. **Décision : rester privé** (pas exposer publiquement les versions exactes de dépendances/structure du JAR à qui voudrait scanner l'image pour des CVE connues). À la place : `docker login ghcr.io` fait **une seule fois à la main sur le VPS** avec un PAT GitHub classique (scope `read:packages` uniquement), lors du setup initial — ce login persiste dans `~/.docker/config.json`, en dehors de `/opt/traderapp/current`, donc jamais écrasé par un déploiement, et le PAT ne transite jamais par un secret GitHub Actions ni par les logs CI (cohérent avec le principe "secrets uniquement sur le VPS").
5. **Timeouts `--wait-timeout` incohérents avec les healthchecks** : avec `db` (retries 20 × interval 10s ≈ 200s pire cas) et `server` (start_period 30s + retries 10 × interval 10s ≈ 130s), un `--wait-timeout 120` expirerait **avant** que Docker ait fini son propre cycle de retries — faux négatif garanti sur un démarrage un peu lent (ex. premier déploiement, init Postgres). Corrigé ci-dessous avec des timeouts revus à la hausse.
6. **`rabbitmq` sans `healthcheck:`** : `server` dépendait de `rabbitmq: {condition: service_started}` — ça veut juste dire "le process a démarré", pas "le broker accepte des connexions". Si RabbitMQ met du temps à être prêt, `server` peut tenter de se connecter trop tôt. Corrigé ci-dessous : `healthcheck:` ajouté à `rabbitmq`, dépendance passée en `service_healthy`.
7. **Pas de nettoyage des images Docker** : chaque déploiement (image pinnée par digest, jamais de tag réutilisé) laisse une image `<none>` (dangling) sur le VPS — rien ne les purge, le disque se remplit avec le temps. Corrigé ci-dessous : `docker image prune -af --filter "until=72h"` en fin de `remote-deploy.sh`, uniquement après un déploiement réussi.

## Limites connues et assumées (pas des bugs, mais à ne pas découvrir en prod)

- **Downtime pendant chaque déploiement** : architecture mono-instance (un seul conteneur `server`, pas de blue-green/rolling). Quand `docker compose up` recrée `server` avec la nouvelle image, il y a une fenêtre (jusqu'à ~130s selon le healthcheck) où Caddy renvoie des 502, faute de second backend vers qui basculer. C'est une conséquence directe de la simplicité voulue pour un projet solo, pas un oubli — à garder en tête pour choisir les horaires de déploiement.
- **Le health-check valide le process, pas la logique métier** : `/actuator/health` confirme que Spring a démarré et que la DB répond, pas que `/api/v1/auth/login` ou la lecture d'un plan de trading fonctionnent réellement après déploiement. "Healthcheck vert" = "appli démarrée", pas "appli fonctionnelle". Un smoke test applicatif post-déploiement serait plus complet mais disproportionné pour l'instant — non inclus dans ce plan.

## Fichiers à créer / modifier

```
server/build.gradle                                              [MODIFIER] + spring-boot-starter-actuator
server/src/main/resources/application-docker.yml                 [MODIFIER] + bloc management: (profil docker uniquement)
server/src/main/java/.../security/SecurityConfig.java             [MODIFIER] + permitAll GET /actuator/health
server/.env.example                                                [MODIFIER] + vars manquantes + commentaire RabbitMQ

deploy/docker-compose.prod.yaml                                    [NOUVEAU]
deploy/Caddyfile                                                    [NOUVEAU]
deploy/server.env.template                                          [NOUVEAU]
deploy/scripts/remote-deploy.sh                                     [NOUVEAU]
deploy/README.md                                                    [NOUVEAU] checklist setup VPS première fois

.github/workflows/deploy.yml                                        [NOUVEAU]
.github/workflows/nightly.yml                                       [MODIFIER] + job backup-db
.github/workflows/backend-checks.yml                                [MODIFIER] + job compose-config sur deploy/docker-compose.prod.yaml (optionnel mais recommandé)
```

## Spring Boot : health-check exploitable

- `server/build.gradle` : `implementation 'org.springframework.boot:spring-boot-starter-actuator'` (pas de version, résolue par le BOM déjà en place).
- `server/src/main/resources/application-docker.yml` : ajouter
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health
    endpoint:
      health:
        show-details: never
  ```
- `SecurityConfig.java` : ajouter avant `.anyRequest().authenticated()` :
  ```java
  .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
  ```

## `deploy/docker-compose.prod.yaml`

Différences clés vs `compose.yaml` (dev, **non touché**) : `server` tiré de GHCR par digest (pas de `build:`), pas de `pgadmin`, `db`/`rabbitmq` sans port publié sur l'hôte (réseau Docker interne uniquement — seul Caddy est exposé), ajout du service `caddy`, `healthcheck:` sur `server` **et sur `rabbitmq`** (gap 6), `server` dépend de `rabbitmq: service_healthy` (pas `service_started`) :

```yaml
name: traderapp
services:
  db:
    image: postgres:16-bookworm
    container_name: traderapp-db
    restart: unless-stopped
    env_file: [shared/server.env]
    volumes: [traderapp_db:/var/lib/postgresql/data]
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $$POSTGRES_USER -d $$POSTGRES_DB"]
      interval: 10s
      timeout: 5s
      retries: 20

  rabbitmq:
    image: rabbitmq:3-management
    container_name: traderapp-rabbitmq
    restart: unless-stopped
    env_file: [shared/server.env]
    environment:
      RABBITMQ_DEFAULT_USER: ${RABBITMQ_USER}
      RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASS}
    volumes: [rabbitmq_data:/var/lib/rabbitmq]
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "-q", "ping"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 20s

  server:
    image: ${SERVER_IMAGE}
    container_name: traderapp_server
    restart: unless-stopped
    env_file: [shared/server.env]
    environment:
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      db: {condition: service_healthy}
      rabbitmq: {condition: service_healthy}
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 30s

  caddy:
    image: caddy:2-alpine
    container_name: traderapp-caddy
    restart: unless-stopped
    ports: ["80:80", "443:443"]
    environment:
      PUBLIC_HOSTNAME: ${PUBLIC_HOSTNAME}
    volumes:
      - ./Caddyfile:/etc/caddy/Caddyfile:ro
      - caddy_data:/data
      - caddy_config:/config
    depends_on:
      server: {condition: service_healthy}

volumes:
  traderapp_db: {}
  rabbitmq_data: {}
  caddy_data: {}
  caddy_config: {}
```

`SERVER_IMAGE`/`PUBLIC_HOSTNAME` sont résolues via le fichier `.env` de substitution de variables de Compose (généré à chaque run par `remote-deploy.sh`, distinct de `env_file:` qui injecte dans les conteneurs).

Pire cas d'attente (utilisé pour calibrer `--wait-timeout` dans `remote-deploy.sh`) : `db` seul ≈ 200s (retries 20 × 10s) ; `rabbitmq` + `server` séquentiels (server ne démarre qu'une fois rabbitmq healthy) ≈ 120s + 130s = 250s.

## `deploy/Caddyfile`

```
{$PUBLIC_HOSTNAME} {
    @actuator path /actuator*
    respond @actuator 404

    reverse_proxy server:8080
}
```

Le `permitAll()` sur `/actuator/health` (nécessaire pour le `wget` interne au conteneur, sans JWT) rendrait sinon l'endpoint accessible publiquement via `https://<ip>.sslip.io/actuator/health`. Ce bloc le bloque à l'edge — le healthcheck Docker ne passe jamais par Caddy (c'est un `wget` du conteneur vers son propre `localhost`), donc ça n'affecte pas `--wait`.

## `deploy/server.env.template`

Fichier de doc uniquement (aucun vrai secret, safe à committer), sur-ensemble de `server/.env.example` : `POSTGRES_*`, `SPRING_DATASOURCE_*`, `SPRING_JPA_HIBERNATE_DDL_AUTO`, `SPRING_FLYWAY_ENABLED`, `SPRING_PROFILES_ACTIVE=docker`, `JWT_SECRET`, `JWT_EXPIRATION_MINUTES`, `MAIL_USERNAME/PASSWORD`, `SPRING_RABBITMQ_*`, `RABBITMQ_USER/PASS` (même commentaire de duplication), `R2_BUCKET`, `RCLONE_CONFIG_R2_ENDPOINT/ACCESS_KEY_ID/SECRET_ACCESS_KEY`, `BACKUP_RETENTION`, `SERVER_PORT`. Pas de `PGADMIN_*` (absent en prod).

## Arborescence sur le VPS

```
/opt/traderapp/
├── current/                    # écrasé intégralement à chaque déploiement — jetable
│   ├── docker-compose.prod.yaml
│   ├── Caddyfile
│   ├── .env                    # régénéré à chaque run (SERVER_IMAGE, PUBLIC_HOSTNAME) — non secret
│   └── scripts/
│       ├── remote-deploy.sh
│       └── backup-db.sh
└── shared/
    └── server.env               # créé une fois à la main, chmod 600, jamais touché par la CI
```

`~/.docker/config.json` (login GHCR, gap 4) vit dans le home du user de déploiement, en dehors de `/opt/traderapp/` — jamais touché par la CI non plus.

Pas de `releases/<n>/` versionnés : puisque le rollback = relancer `deploy.yml` avec un `manifest_version` antérieur, l'historique pertinent vit dans git (`deploy/manifests/`), pas sur le VPS — `current/` reste sans état, aucune logique de nettoyage à écrire (au-delà du `docker image prune` du gap 7).

## Copie vers le VPS : `appleboy/ssh-action` + `appleboy/scp-action`

Choisis pour : même mainteneur (params d'auth cohérents host/username/key/port), `script_stop: true` propage bien l'échec de `docker compose up --wait` dans le job, `envs:` permet d'injecter des valeurs calculées sans les écrire en dur. **Pinner les deux actions par SHA de commit complet** (pas juste `@v1`), vu qu'elles manipulent la clé SSH privée.

Séquence en 3 étapes (garantit que `current/` ne s'accumule jamais et que `shared/` n'est jamais touché) :
1. `ssh-action` : `mkdir -p /opt/traderapp/current/scripts /opt/traderapp/shared && rm -rf /opt/traderapp/current/*`
2. `scp-action` : source `deploy/docker-compose.prod.yaml,deploy/Caddyfile,deploy/scripts` → target `/opt/traderapp/current`
3. `scp-action` : source `.github/scripts/backup-db.sh` → target `/opt/traderapp/current/scripts`

**Point le plus fragile de tout le design** : le comportement exact d'aplatissement du `scp` (est-ce que `deploy/scripts` atterrit en `current/scripts/` ou `current/deploy/scripts/` ?). À vérifier en isolation contre le vrai VPS avant de faire confiance au reste (voir Vérification, étape VPS 3).

## `.github/workflows/deploy.yml`

```yaml
name: Deploy
on:
  workflow_dispatch:
    inputs:
      manifest_version:
        description: 'Version à déployer (ex: 0.0.15). Vide = dernière. Version antérieure = rollback manuel.'
        required: false
        default: ''
concurrency:
  group: deploy
  cancel-in-progress: false
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v7
      - name: Resolve manifest
        id: manifest
        run: |
          INPUT="${{ inputs.manifest_version }}"
          if [ -z "$INPUT" ]; then
            VERSION=$(find deploy/manifests -maxdepth 1 -name 'manifest-*.yaml' | sed -E 's/.*manifest-([0-9]+\.[0-9]+\.[0-9]+)\.yaml/\1/' | sort -V | tail -1)
            [ -n "$VERSION" ] || { echo "::error::No manifests found."; exit 1; }
          else
            VERSION="$INPUT"
          fi
          MANIFEST_PATH="deploy/manifests/manifest-${VERSION}.yaml"
          [ -f "$MANIFEST_PATH" ] || { echo "::error::Manifest '${MANIFEST_PATH}' does not exist."; exit 1; }
          IMAGE=$(grep 'image:' "$MANIFEST_PATH" | awk '{print $2}')
          echo "version=$VERSION" >> "$GITHUB_OUTPUT"
          echo "image=$IMAGE" >> "$GITHUB_OUTPUT"
      # step clean + scp bundle (voir section copie ci-dessus)
      - name: Run remote deploy
        uses: appleboy/ssh-action@<sha épinglé>
        with:
          host: ${{ secrets.VPS_HOST }}
          username: ${{ secrets.VPS_USER }}
          key: ${{ secrets.VPS_SSH_PRIVATE_KEY }}
          port: ${{ secrets.VPS_SSH_PORT }}
          script_stop: true
          envs: SERVER_IMAGE,PUBLIC_HOSTNAME,MANIFEST_VERSION
          script: |
            chmod +x /opt/traderapp/current/scripts/remote-deploy.sh
            /opt/traderapp/current/scripts/remote-deploy.sh
        env:
          SERVER_IMAGE: ${{ steps.manifest.outputs.image }}
          PUBLIC_HOSTNAME: ${{ vars.PUBLIC_HOSTNAME }}
          MANIFEST_VERSION: ${{ steps.manifest.outputs.version }}
```

Pas besoin du token d'app GitHub (`CD_APP_ID`) : ce workflow ne fait que lire `deploy/manifests/`, jamais de commit — le `GITHUB_TOKEN` par défaut suffit.

## `deploy/scripts/remote-deploy.sh` (tourne sur le VPS)

```sh
#!/usr/bin/env sh
set -eu

require_env() {
  name="$1"; eval "value=\${$name:-}"
  [ -n "$value" ] || { echo "Error: $name is required." >&2; exit 1; }
}
require_env SERVER_IMAGE
require_env PUBLIC_HOSTNAME
require_env MANIFEST_VERSION

BUNDLE_DIR="/opt/traderapp/current"
SHARED_ENV="/opt/traderapp/shared/server.env"

if [ ! -f "$SHARED_ENV" ]; then
  echo "Error: $SHARED_ENV not found. Create it manually first (see deploy/README.md)." >&2
  exit 1
fi

cd "$BUNDLE_DIR"
chmod +x scripts/backup-db.sh

printf 'SERVER_IMAGE=%s\nPUBLIC_HOSTNAME=%s\n' "$SERVER_IMAGE" "$PUBLIC_HOSTNAME" > .env

echo "==> Ensuring database is up..."
# db seul : pire cas healthcheck ~200s (retries 20 x interval 10s) -> marge à 220s
docker compose --env-file .env -f docker-compose.prod.yaml up -d --wait --wait-timeout 220 db

echo "==> Running pre-deploy backup..."
set -a
. "$SHARED_ENV"
set +a
POSTGRES_CONTAINER=traderapp-db sh scripts/backup-db.sh

echo "==> Deploying manifest ${MANIFEST_VERSION} (${SERVER_IMAGE})..."
# tous services : rabbitmq (~120s) puis server (~130s) séquentiels dans le pire cas -> marge à 280s
docker compose --env-file .env -f docker-compose.prod.yaml up -d --wait --wait-timeout 280 --remove-orphans

echo "==> Deploy of manifest ${MANIFEST_VERSION} complete."

echo "==> Pruning dangling images..."
docker image prune -af --filter "until=72h"

echo "==> Done."
```

Points clés :
- **Timeouts recalibrés (gap 5)** : `--wait-timeout` toujours strictement au-dessus du pire cas du healthcheck concerné, pour ne jamais faire échouer le job GitHub Actions par faux négatif sur un démarrage juste un peu lent.
- **`db` toujours démarré, backupé, puis tout est déployé** — y compris au tout premier déploiement (le conteneur `db` n'existe pas encore). Pas de branche spéciale "premier déploiement" : le premier backup capture juste une base vide/fraîchement migrée, ce qui est inoffensif, et la logique reste identique à chaque run.
- `shared/server.env` est sourcé dans le shell hôte (pas via `env_file:`) uniquement pour que les `require_env` de `backup-db.sh` (qui tourne sur l'hôte, pas dans un conteneur) voient les valeurs.
- **`docker image prune` en fin de script (gap 7)** : uniquement après un déploiement réussi (le seul moment où de nouvelles images dangling apparaissent), filtré sur `until=72h` pour ne jamais toucher une image encore potentiellement utile à un rollback tout juste effectué.

## `.github/workflows/nightly.yml` — job backup indépendant

Nouveau job sibling de `npm-audit`/`trivy`, sans dépendance :

```yaml
  backup-db:
    name: Nightly DB backup
    runs-on: ubuntu-latest
    steps:
      - name: Run backup on VPS
        uses: appleboy/ssh-action@<sha épinglé>
        with:
          host: ${{ secrets.VPS_HOST }}
          username: ${{ secrets.VPS_USER }}
          key: ${{ secrets.VPS_SSH_PRIVATE_KEY }}
          port: ${{ secrets.VPS_SSH_PORT }}
          script_stop: true
          script: |
            set -a
            . /opt/traderapp/shared/server.env
            set +a
            POSTGRES_CONTAINER=traderapp-db sh /opt/traderapp/current/scripts/backup-db.sh
```

Réutilise les mêmes secrets SSH que `deploy.yml`, indépendant de toute logique de déploiement — comble le trou "pas de backup un jour sans déploiement".

## Secrets / variables GitHub à créer manuellement (je ne peux pas les créer)

- Secrets : `VPS_HOST`, `VPS_USER`, `VPS_SSH_PRIVATE_KEY`, `VPS_SSH_PORT` (si ≠ 22)
- Variable (pas secret) : `PUBLIC_HOSTNAME` = `<ip-avec-tirets>.sslip.io`

## Checklist de mise en place manuelle sur le VPS (avant le premier vrai déploiement)

1. Vérifier `docker --version && docker compose version`.
2. `mkdir -p /opt/traderapp/current/scripts /opt/traderapp/shared`.
3. Copier `deploy/server.env.template` vers `/opt/traderapp/shared/server.env`, éditer avec les vraies valeurs, `chmod 600`.
4. Générer une clé SSH **dédiée à la CI** (pas la clé perso) : `ssh-keygen -t ed25519 -f traderapp-cd -C traderapp-cd`, ajouter la clé publique à `~/.ssh/authorized_keys` sur le VPS.
5. Ouvrir le firewall pour 80/443 (et le port SSH si non-standard).
6. **Package GHCR privé (gap 4)** : créer un PAT GitHub classique (scope `read:packages` uniquement), puis sur le VPS : `docker login ghcr.io -u <ton-user-github>` (coller le PAT au prompt, jamais en argument en clair). Persiste dans `~/.docker/config.json`, à refaire seulement si le PAT expire/est révoqué.
7. Créer les secrets/variable GitHub listés ci-dessus.
8. Faire le test SSH isolé (voir Vérification) avant de lancer le vrai `deploy.yml`.

## Vérification

**En local/CI (avant de toucher au VPS), dans cet ordre :**
1. `cd server && ./gradlew bootJar` — la dépendance actuator se résout, l'appli build toujours.
2. Lancer l'appli en local (profil dev), `curl -i http://localhost:8080/actuator/health` sans auth → attendre `200 {"status":"UP"}` (valide le fix SecurityConfig).
3. Builder l'image de prod en local (`docker build -t traderapp-server:test server`), puis `docker exec <container> wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health; echo $?` — valide la commande healthcheck contre le vrai binaire BusyBox avant de lui faire confiance dans le compose.
4. Étendre le job `compose-config` existant (`backend-checks.yml`) à `deploy/docker-compose.prod.yaml`, avec un `.env` jetable (`SERVER_IMAGE=nginx:alpine`, `PUBLIC_HOSTNAME=test.example.com`) et un `shared/server.env` copié depuis le template — attrape les erreurs de syntaxe/interpolation avant le VPS.
5. Valider le Caddyfile : `docker run --rm -e PUBLIC_HOSTNAME=test.example.com -v "$(pwd)/deploy/Caddyfile:/etc/caddy/Caddyfile" caddy:2-alpine caddy validate --config /etc/caddy/Caddyfile` (`PUBLIC_HOSTNAME` doit être défini, sinon Caddy interprète mal tout le fichier).
6. Optionnel : `docker compose -f deploy/docker-compose.prod.yaml up -d --wait` entièrement en local avec l'image de test — valide db→rabbitmq→server→caddy ensemble (pas de vrai cert TLS avec un faux hostname, mais confirme l'absence de crash-loop, et que la nouvelle dépendance `rabbitmq: service_healthy` ne bloque pas indéfiniment).

**Contre le vrai VPS (uniquement vérifiable là), dans cet ordre :**
1. Test de connectivité SSH isolé : un run minimal (`echo ok && whoami && docker compose version`) via `ssh-action` — valide les 4 secrets avant toute logique compose/backup.
2. Checklist de mise en place manuelle (section précédente), **y compris le `docker login ghcr.io`** — sans lui, le premier `docker compose up` échouera au `pull` de l'image privée.
3. Vérifier la copie SCP : lancer les 3 étapes de copie puis `ssh` + `ls -laR /opt/traderapp/current` pour confirmer l'arborescence exacte (point le plus fragile du design, cf. section copie).
4. Premier vrai `deploy.yml`, `manifest_version` vide. Vérifier : le backup réussit (nouvel objet `traderapp-<timestamp>.sql.gz` sur R2), `docker compose up --wait` réussit dans les temps (surveiller si les nouveaux timeouts 220s/280s sont confortables ou trop larges en pratique), puis depuis un poste externe `curl -i https://<ip-avec-tirets>.sslip.io/actuator/health` (HTTPS + reverse proxy) et `curl -i https://.../actuator/env` (doit 404 — valide le blocage Caddy).
5. Répétition rollback : après un 2e manifest publié, relancer `deploy.yml` avec l'ancienne version, vérifier via `docker inspect traderapp_server --format '{{.Image}}'` sur le VPS que le digest antérieur tourne bien.
6. Vérifier l'indépendance du backup nightly : déclencher `nightly.yml` manuellement sans déploiement récent, confirmer qu'un backup atterrit sur R2.
7. Vérifier le `docker image prune` : après 2-3 déploiements, `docker images` sur le VPS ne doit pas accumuler d'images `<none>` de plus de 72h.
