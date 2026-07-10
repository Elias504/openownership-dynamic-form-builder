#!/usr/bin/env bash
# PostgreSQL initialization script.
# Runs once when the data directory is first created (docker-entrypoint-initdb.d).
# Ensures pg_hba.conf allows password-authenticated connections from the Docker
# internal network without SSL, and that the postgres role is active.

set -euo pipefail

# Allow password authentication from any host (Docker internal network) without SSL.
# The official postgres image already writes a permissive pg_hba.conf when
# POSTGRES_HOST_AUTH_METHOD is not set, but this makes it explicit and ensures
# sslmode=disable connections are accepted.
cat >> "${PGDATA}/pg_hba.conf" <<EOF

# Allow password authentication from Docker internal networks without SSL.
host    all             all             0.0.0.0/0               md5
EOF

# Ensure the postgres role is enabled with LOGIN and the configured password.
psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname "${POSTGRES_DB}" <<SQL
ALTER ROLE "${POSTGRES_USER}" WITH LOGIN PASSWORD '${POSTGRES_PASSWORD}';
SQL
