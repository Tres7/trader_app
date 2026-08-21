#!/usr/bin/env sh
set -eu

require_env() {
  name="$1"
  eval "value=\${$name:-}"
  [ -n "$value" ] || {
    echo "Error: $name is required." >&2
    exit 1
  }
}

require_env POSTGRES_CONTAINER
require_env POSTGRES_DB
require_env POSTGRES_USER
require_env R2_BUCKET
require_env RCLONE_CONFIG_R2_ENDPOINT
require_env RCLONE_CONFIG_R2_ACCESS_KEY_ID
require_env RCLONE_CONFIG_R2_SECRET_ACCESS_KEY

export RCLONE_CONFIG_R2_TYPE=s3
export RCLONE_CONFIG_R2_PROVIDER=Cloudflare
export RCLONE_CONFIG_R2_ENDPOINT
export RCLONE_CONFIG_R2_ACCESS_KEY_ID
export RCLONE_CONFIG_R2_SECRET_ACCESS_KEY
# Le token R2 n'a que des droits objet (pas de gestion de bucket) : sans ce flag,
# rclone tente un appel CreateBucket avant l'upload et se prend un Access Denied,
# meme si le bucket existe deja.
export RCLONE_CONFIG_R2_NO_CHECK_BUCKET=true

KEEP="${BACKUP_RETENTION:-10}"

TIMESTAMP=$(date -u +%Y%m%dT%H%M%SZ)
DUMP_NAME="traderapp-${TIMESTAMP}.sql.gz"
DUMP_FILE="/tmp/${DUMP_NAME}"

docker exec "$POSTGRES_CONTAINER" pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" | gzip > "$DUMP_FILE"

rclone copyto "$DUMP_FILE" "r2:${R2_BUCKET}/backups/${DUMP_NAME}"
rm -f "$DUMP_FILE"

rclone lsf "r2:${R2_BUCKET}/backups/" | sort | head -n "-${KEEP}" | while IFS= read -r old; do
  [ -n "$old" ] && rclone deletefile "r2:${R2_BUCKET}/backups/${old}"
done

echo "Backup complete: ${DUMP_NAME}"
