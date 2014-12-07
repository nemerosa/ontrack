# Environment

REPOSITORY=/var/lib/jenkins/repository/ontrack/2.0

# Cleanup

rm -rf ${WORKSPACE}/*

# Extract the Docker delivery JAR

unzip ${REPOSITORY}/ontrack-delivery-docker-${ONTRACK_VERSION_FULL}.jar -d ${WORKSPACE}

# Upload the script

scp production-update.sh root@ontrack.nemerosa.net:~

# Runs the script remotely

ssh root@ontrack.nemerosa.net ./production-update.sh --version=${ONTRACK_VERSION_FULL}
