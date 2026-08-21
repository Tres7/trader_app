#!/usr/bin/env sh
set -eu

# rclone peut vivre dans ~/bin (install sans sudo) plutot que dans le PATH systeme -
# a inclure explicitement, la session SSH non-interactive ne charge pas ~/.bashrc.
export PATH="$HOME/bin:$PATH"

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

# Charge shared/server.env dans l'environnement du shell sans passer par `source`/`.`
# (des valeurs comme un mot de passe d'app Gmail contiennent des espaces, ce qui casse
# l'interpretation shell classique d'un fichier KEY=VALUE). Necessaire a la fois pour
# que Compose resolve les ${VAR} du compose file (ex. RABBITMQ_USER/PASS) et pour que
# backup-db.sh voie POSTGRES_DB/USER, R2_*, etc. via son require_env.
set -a
while IFS='=' read -r key value; do
  case "$key" in
    ''|'#'*) continue ;;
  esac
  export "$key=$value"
done < "$SHARED_ENV"
set +a

printf 'SERVER_IMAGE=%s\nPUBLIC_HOSTNAME=%s\n' "$SERVER_IMAGE" "$PUBLIC_HOSTNAME" > .env

echo "==> Ensuring database is up..."
# db seul : pire cas healthcheck ~200s (retries 20 x interval 10s) -> marge a 220s
docker compose --env-file .env -f docker-compose.prod.yaml up -d --wait --wait-timeout 220 db

echo "==> Running pre-deploy backup..."
POSTGRES_CONTAINER=traderapp-db sh scripts/backup-db.sh

echo "==> Deploying manifest ${MANIFEST_VERSION} (${SERVER_IMAGE})..."
# tous services : rabbitmq (~120s) puis server (~130s) sequentiels dans le pire cas -> marge a 280s
docker compose --env-file .env -f docker-compose.prod.yaml up -d --wait --wait-timeout 280 --remove-orphans

echo "==> Deploy of manifest ${MANIFEST_VERSION} complete."

echo "==> Pruning dangling images..."
docker image prune -af --filter "until=72h"

echo "==> Done."
