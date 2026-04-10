#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# Plugwerk Server — Start Script
# ---------------------------------------------------------------------------
# Usage:
#   ./start.sh                          Start with defaults
#   PLUGWERK_DB_URL=... ./start.sh      Override database URL
#   JAVA_OPTS="-Xmx2g" ./start.sh      Override JVM options
# ---------------------------------------------------------------------------

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$(ls "$SCRIPT_DIR"/plugwerk-server-backend-*.jar 2>/dev/null | head -1)"

if [[ -z "$JAR" ]]; then
  echo "ERROR: No plugwerk-server-backend-*.jar found in $SCRIPT_DIR" >&2
  exit 1
fi

# JVM defaults — override with JAVA_OPTS environment variable
: "${JAVA_OPTS:=-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8}"

echo "Starting Plugwerk Server..."
echo "  JAR:       $(basename "$JAR")"
echo "  JAVA_OPTS: $JAVA_OPTS"
echo ""

# shellcheck disable=SC2086
exec java $JAVA_OPTS -jar "$JAR" "$@"
