#!/usr/bin/env bash
set -euo pipefail

export SCANNER_PORT="${SCANNER_PORT:-50051}"
export SCANNER_HOST="${SCANNER_HOST:-localhost}"
export SERVER_PORT="${PORT:-8080}"
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:--XX:+UseSerialGC -Xss512k -Xms32m -Xmx256m}"

cd /app

(
  cd mtg-scanner
  source .venv/bin/activate
  python grpc_server.py --port "${SCANNER_PORT}"
) &
SCANNER_PID="$!"

cleanup() {
  kill "${SCANNER_PID}" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

exec java \
  -jar app.jar \
  --server.port="${SERVER_PORT}" \
  --scanner.host="${SCANNER_HOST}" \
  --scanner.port="${SCANNER_PORT}"
