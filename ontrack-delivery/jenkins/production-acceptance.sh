# Environment

REPOSITORY=/var/lib/jenkins/repository/ontrack/2.0

# Cleanup

rm -rf ${WORKSPACE}/*

# Extract the Docker delivery JAR

unzip ${REPOSITORY}/ontrack-delivery-docker-${ONTRACK_VERSION_FULL}.jar -d ${WORKSPACE}

# Running the smoke tests

cd ${WORKSPACE}
${WORKSPACE}/acceptance.sh \
    --jar=${REPOSITORY}/ontrack-acceptance-${ONTRACK_VERSION_FULL}.jar \
    --ontrack-context=production \
    --ontrack-url=https://ontrack.nemerosa.net

