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
7. Dans GitHub (Settings → Secrets and variables → Actions) :
   - Secrets : `VPS_HOST`, `VPS_USER`, `VPS_SSH_PRIVATE_KEY`, `VPS_SSH_PORT` (si ≠ 22)
   - Variable : `PUBLIC_HOSTNAME` = `<ip-avec-tirets>.sslip.io` (ex. `203-0-113-45.sslip.io`)
8. Vérifier la connectivité SSH avant de lancer un vrai déploiement (voir plus bas).

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
