#!/usr/bin/env bash
set -euo pipefail

SOURCE_REVISION="$1"
IMAGE="$2"

mkdir -p deploy/manifests

LAST=$(find deploy/manifests -maxdepth 1 -name 'manifest-*.yaml' 2>/dev/null | sed -E 's/.*manifest-([0-9]+\.[0-9]+\.[0-9]+)\.yaml/\1/' | sort -V | tail -1)

if [ -z "$LAST" ]; then
  NEXT="0.0.1"
else
  PATCH=$(echo "$LAST" | cut -d. -f3)
  MAJOR_MINOR=$(echo "$LAST" | cut -d. -f1-2)
  NEXT="${MAJOR_MINOR}.$((PATCH + 1))"
fi

cat > "deploy/manifests/manifest-${NEXT}.yaml" <<EOF
schemaVersion: 1
manifestVersion: ${NEXT}
services:
  server:
    version: ${NEXT}
    sourceRevision: ${SOURCE_REVISION}
    image: ${IMAGE}
EOF

echo "$NEXT"
