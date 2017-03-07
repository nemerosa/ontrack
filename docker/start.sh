#!/usr/bin/env bash

# Starting script for Ontrack

# Preparation of the configuration
rm -rf /opt/ontrack/config \
    && cp -R /var/ontrack/conf /opt/ontrack/config

# Launching the application
exec java \
    -Dloader.path=${EXTENSIONS_DIR} \
    ${JAVA_OPTIONS} \
    -jar /opt/ontrack/ontrack.jar \
    "--spring.profiles.active=${PROFILE}" \
    "--spring.datasource.url=jdbc:h2:/var/ontrack/data/database/data;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;DEFRAG_ALWAYS=TRUE" \
    "--ontrack.config.applicationWorkingDir=/var/ontrack/data" \
    "--logging.file=/var/ontrack/data/log/ontrack.log"
