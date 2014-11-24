#!/bin/bash

# Parameters
# * ONTRACK_VERSION_FULL
# * ONTRACK_VERSION_COMMIT
# * ONTRACK_VERSION_BASE
# * ONTRACK_VERSION_BUILD

# Globals
# * DOCKER_PASSWORD - Docker password for `nemerosa` in Docker Hub

# Script

docker tag ontrack:${ONTRACK_VERSION_FULL} nemerosa/ontrack:${ONTRACK_VERSION_FULL}
docker login --email="***" --username="nemerosa" --password="${DOCKER_PASSWORD}"
docker push nemerosa/ontrack:${ONTRACK_VERSION_FULL}
docker logout
