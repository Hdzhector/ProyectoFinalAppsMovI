#!/usr/bin/env sh
# Lanzador de Gradle 8.13. El archivo JAR oficial se obtiene en la primera ejecución
# porque este paquete de código fuente no incluye binarios externos.
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -s "$JAR" ]; then
  echo "Descargando el Gradle Wrapper oficial (primera ejecución)..."
  if ! command -v curl >/dev/null 2>&1; then
    echo "Necesitas curl o generar el wrapper con: gradle wrapper --gradle-version 8.13" >&2
    exit 1
  fi
  curl --fail --location --retry 2 --silent --show-error \
    "https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradle/wrapper/gradle-wrapper.jar" \
    --output "$JAR.tmp"
  mv "$JAR.tmp" "$JAR"
fi
exec java -classpath "$JAR" org.gradle.wrapper.GradleWrapperMain "$@"
