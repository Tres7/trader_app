# Déploiement VPS

`deploy.yml` déploie `server` sur le VPS via SSH, en s'appuyant sur les manifests
versionnés (`deploy/manifests/`), un reverse proxy Caddy (HTTPS auto via sslip.io)
et un backup obligatoire avant chaque déploiement.

## Mise en place manuelle (une seule fois, avant le premier déploiement)

1. Vérifier que Docker + le plugin Compose sont installés sur le VPS :
   ```
   docker --version && docker compose version
   ```
2. Créer l'arborescence :
   ```
   mkdir -p /opt/traderapp/current/scripts /opt/traderapp/shared
   ```
3. Copier `deploy/server.env.template` vers `/opt/traderapp/shared/server.env`,
   remplir avec les vraies valeurs, puis :
   ```
   chmod 600 /opt/traderapp/shared/server.env
   ```
4. Générer une clé SSH dédiée à la CI (pas ta clé perso) :
   ```
   ssh-keygen -t ed25519 -f traderapp-cd -C traderapp-cd
   ```
   Ajouter la clé publique à `~/.ssh/authorized_keys` sur le VPS.
5. Ouvrir le firewall pour les ports 80/443 (et le port SSH si non-standard).
6. Le package GHCR `server` reste privé. Sur le VPS, une seule fois :
   ```
   docker login ghcr.io -u <ton-user-github>
   ```
   Coller un PAT GitHub classique (scope `read:packages` uniquement) au prompt —
   jamais en argument en clair. Persiste dans `~/.docker/config.json`, en dehors
   de `/opt/traderapp/`, jamais touché par la CI.
7. Installer `rclone` sur le VPS (utilisé par `backup-db.sh` pour uploader vers R2).
   Si tu as les droits `sudo` sur le VPS :
   ```
   sudo -v ; curl https://rclone.org/install.sh | sudo bash
   ```
   Sinon (pas d'accès `sudo`), installer sans droits root dans `~/bin` — `remote-deploy.sh`
   ajoute déjà `$HOME/bin` au `PATH` avant d'appeler `backup-db.sh`, donc rien d'autre à configurer :
   ```
   mkdir -p ~/bin
   curl -o ~/bin/rclone.zip https://downloads.rclone.org/rclone-current-linux-amd64.zip
   cd ~/bin
   python3 -m zipfile -e rclone.zip .   # si `unzip` n'est pas dispo non plus
   mv rclone-*-linux-amd64/rclone ~/bin/rclone
   chmod +x ~/bin/rclone
   rm -rf rclone.zip rclone-*-linux-amd64
   rclone version   # doit afficher un numéro de version
   ```
8. Dans GitHub (Settings → Secrets and variables → Actions) :
   - Secrets : `VM_HOST`, `VM_USER`, `SSH_PRIVATE_KEY`, `VM_PORT` (si ≠ 22)
   - Variable : `PUBLIC_HOSTNAME` = `<ip-avec-tirets>.sslip.io` (ex. `203-0-113-45.sslip.io`)
9. Vérifier la connectivité SSH avant de lancer un vrai déploiement (voir plus bas).

## Arborescence sur le VPS

```
/opt/traderapp/
├── current/          # écrasé intégralement à chaque déploiement — jetable
│   ├── docker-compose.prod.yaml
│   ├── Caddyfile
│   ├── .env           # régénéré à chaque run (SERVER_IMAGE, PUBLIC_HOSTNAME) — non secret
│   └── scripts/
│       ├── remote-deploy.sh
│       └── backup-db.sh
└── shared/
    └── server.env      # créé une fois à la main, chmod 600, jamais touché par la CI
```

## Déployer / rollback

`deploy.yml` (workflow_dispatch) :
- `manifest_version` vide → déploie le dernier manifest publié.
- `manifest_version` = une version antérieure (ex. `0.0.12`) → rollback manuel en
  redéployant cet ancien manifest. Pas de rollback automatique (voir le plan pour
  le raisonnement : DB non réversible avec Flyway, risque de boucle si le
  health-check est mal calibré, visibilité nécessaire en solo).

## Backups

`backup-db.sh` tourne :
- avant chaque déploiement (obligatoire, le déploiement s'arrête s'il échoue) ;
- indépendamment, chaque nuit (`nightly.yml`), pour ne pas dépendre de la
  fréquence des déploiements.

## Limites connues

- **Downtime pendant chaque déploiement** (jusqu'à ~2 minutes) : archi
  mono-instance, pas de blue-green. Caddy renvoie des 502 pendant que `server`
  redémarre.
- **`/actuator/health` valide que l'appli a démarré, pas qu'elle fonctionne
  réellement** (pas de smoke test applicatif après déploiement).

## Troubleshooting

Pièges réels rencontrés en mettant ce pipeline en place — à vérifier en premier
avant de creuser plus loin.

### `server` ou `caddy` semble ignorer un changement de config

Compose ne recrée un conteneur que si **son image ou sa config déclarée dans le
compose file** a changé. Un changement de `Caddyfile` (monté en bind-mount, pas
dans l'image) ou un redéploiement de la même image applicative (ex. plusieurs
tentatives de debug sans nouveau code) ne déclenche donc **pas** de recréation
automatique — le conteneur continue de tourner avec son ancienne config en
mémoire, silencieusement.

`remote-deploy.sh` force déjà la recréation de `server` et `caddy`
(`--force-recreate --no-deps`) à chaque déploiement pour cette raison précise.
Si un souci de ce type apparaît quand même (ex. après une modif manuelle sur le
VPS), forcer à la main :
```bash
docker compose -f /opt/traderapp/current/docker-compose.prod.yaml up -d --force-recreate --no-deps <service>
```

### RabbitMQ refuse l'authentification (`ACCESS_REFUSED`, `invalid credentials`)

Deux causes possibles, dans cet ordre de probabilité :

1. **Mot de passe avec caractères spéciaux** (`/`, `+`, `=` — typiques d'un
   `openssl rand -base64`) : l'image RabbitMQ officielle échoue **silencieusement**
   à créer l'utilisateur par défaut avec ces caractères dans `RABBITMQ_DEFAULT_PASS`.
   Aucune erreur au démarrage, l'authentification échoue seulement ensuite.
   → Toujours générer ces mots de passe avec `openssl rand -hex 24` (alphanumérique).

2. **Conteneur/volume RabbitMQ déjà initialisé avec d'anciens identifiants** :
   `RABBITMQ_DEFAULT_USER`/`PASS` ne sont utilisés par l'image qu'au tout premier
   démarrage (volume Mnesia vide). Changer `shared/server.env` après coup ne fait
   rien tant que le volume existant n'est pas supprimé :
   ```bash
   docker stop traderapp-rabbitmq
   docker rm traderapp-rabbitmq
   docker volume rm traderapp_rabbitmq_data
   ```
   Puis relancer un déploiement complet pour recréer RabbitMQ à partir d'un
   volume neuf.

Pour vérifier directement ce que RabbitMQ a réellement en mémoire (sans passer
par Spring) :
```bash
docker exec traderapp-rabbitmq rabbitmqctl list_users
docker exec traderapp-rabbitmq rabbitmqctl authenticate_user <user> <password>
```

### `ERR_SSL_PROTOCOL_ERROR` / certificat non valide dans le navigateur

- Vérifier que l'URL testée utilise bien le **même hostname** que la variable
  GitHub `PUBLIC_HOSTNAME` (ex. `217-154-19-172.sslip.io` avec des **tirets** —
  `217.154.19.172.sslip.io` avec des points est un hostname *différent* du point
  de vue de Caddy, donc sans certificat pour lui).
- Vérifier les logs Caddy pour confirmer l'émetteur du certificat :
  ```bash
  docker compose -f /opt/traderapp/current/docker-compose.prod.yaml logs caddy --tail 100
  ```
  `"issuer":"acme-v02.api.letsencrypt.org-directory"` = vrai certificat Let's Encrypt, tout va bien.
  `"issuer":"local"` = Caddy est retombé sur sa CA interne auto-signée, généralement parce
  que `PUBLIC_HOSTNAME` n'était pas un nom de domaine valide (ex. une IP brute sans
  `.sslip.io`) au moment de l'obtention du certificat.
- Si le serveur a bien un vrai certificat mais que le navigateur affiche quand même
  une erreur : tester en navigation privée — Chrome met parfois en cache une
  tentative de connexion ratée vers l'ancien certificat auto-signé.

### `rclone: not found` pendant le backup

`rclone` n'est pas installé par défaut sur le VPS — voir l'étape 7 de la checklist
ci-dessus. `remote-deploy.sh` ajoute `~/bin` au `PATH`, donc si `rclone` est
installé ailleurs, soit l'y déplacer, soit adapter le script.

### Valider le `Caddyfile` sans toucher au VPS

```bash
docker run --rm -e PUBLIC_HOSTNAME=test.example.com -v "$(pwd)/deploy/Caddyfile:/etc/caddy/Caddyfile" caddy:2-alpine caddy validate --config /etc/caddy/Caddyfile
```
`PUBLIC_HOSTNAME` doit être défini, sinon Caddy interprète mal tout le fichier
(le bloc de site devient vide, les matchers sont lus comme des options globales
invalides).
