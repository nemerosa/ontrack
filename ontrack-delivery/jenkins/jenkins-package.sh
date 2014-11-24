#!/bin/bash

# Parameters
# * LOCAL_BRANCH
# * REMOTE_BRANCH

# Built-in
# * ${WORKSPACE}

# Misc.
# Git: from REMOTE_BRANCH to LOCAL_BRANCH
# Post action: JUnit of **/build/test-results/*.xml

# Script (Gradle)

./gradlew clean displayVersion integrationTest --info --profile

# Followed by:

./gradlew release writeVersion -x test --info --profile

# Followed by (success only):

ontrack-delivery/archive.sh --source=${WORKSPACE} --destination=/var/lib/jenkins/repository/ontrack/2.0

# Following by (injection of):

version.properties

