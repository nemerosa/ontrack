#!/bin/bash

# Parameters
# * ONTRACK_VERSION_FULL
# * ONTRACK_VERSION_COMMIT
# * ONTRACK_VERSION_BASE
# * ONTRACK_VERSION_BUILD

# Built-in
# * ${WORKSPACE}

# Misc.
# * Running with Xvfb (1024x768x24, offset: 1)
# * Post action: JUnit tests with ontrack-acceptance.xml

# Script

# Environment

REPOSITORY=/var/lib/jenkins/repository/ontrack/2.0

# Cleanup

rm -rf ${WORKSPACE}/*

# Extract the Docker delivery JAR

unzip ${REPOSITORY}/ontrack-delivery-docker-${ONTRACK_VERSION_FULL}.jar -d ${WORKSPACE}

# Execution of tests

cd ${WORKSPACE}
${WORKSPACE}/docker-acceptance.sh \
    --jar=${REPOSITORY}/ontrack-ui-${ONTRACK_VERSION_FULL}.jar \
    --acceptance=${REPOSITORY}/ontrack-acceptance-${ONTRACK_VERSION_FULL}.jar \
    --docker-user=`id -u jenkins`
