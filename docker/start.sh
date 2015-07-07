#!/usr/bin/env bash

# Starting script for Ontrack

# Preparation of the configuration
rm -rf /opt/ontrack/config \
    && cp -R /var/ontrack/conf /opt/ontrack/config

# Launching the application
java -jar /opt/ontrack/ontrack.jar \
    --spring.profiles.active=${PROFILE} \
    --ontrack.config.applicationWorkingDir=/var/ontrack/data \
    --logging.path=/var/ontrack/data/log/
