#!/usr/bin/env bash

# Starting script for Ontrack

# Preparation of the configuration
rm -rf /opt/ontrack/config \
    && cp -R /var/ontrack/conf /opt/ontrack/config

# Traces
echo "[START] JAVA_OPTIONS   = ${JAVA_OPTIONS}"
echo "[START] ONTRACK_ARGS   = ${ONTRACK_ARGS}"
echo "[START] PROFILE        = ${PROFILE}"

# Launching the application
exec java \
    ${JAVA_OPTIONS} \
    -jar /opt/ontrack/ontrack.jar \
    "--spring.profiles.active=${PROFILE}" \
    "--ontrack.config.applicationWorkingDir=/var/ontrack/data" \
    ${ONTRACK_ARGS}
