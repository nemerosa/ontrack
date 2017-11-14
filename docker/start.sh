#!/usr/bin/env bash

# Starting script for Ontrack

# Preparation of the configuration
rm -rf /opt/ontrack/config \
    && cp -R /var/ontrack/conf /opt/ontrack/config

# Traces
echo "[START] EXTENSIONS_DIR = ${EXTENSIONS_DIR}"
echo "[START] JAVA_OPTIONS = ${JAVA_OPTIONS}"
echo "[START] ONTRACK_ARGS = ${ONTRACK_ARGS}"

# Launching the application
exec java \
    -Dloader.path=${EXTENSIONS_DIR} \
    ${JAVA_OPTIONS} \
    -jar /opt/ontrack/ontrack.jar \
    "--spring.profiles.active=${PROFILE}" \
    "--ontrack.config.applicationWorkingDir=/var/ontrack/data" \
    "--logging.file=/var/ontrack/data/log/ontrack.log" \
    ${ONTRACK_ARGS}
