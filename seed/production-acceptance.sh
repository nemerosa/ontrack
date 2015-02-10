# Environment

REPOSITORY=/var/lib/jenkins/repository/ontrack/2.0

# Cleanup

rm -rf ${WORKSPACE}/*

# Extract the Docker delivery JAR

unzip ${REPOSITORY}/ontrack-delivery-docker-${VERSION_FULL}.jar -d ${WORKSPACE}

# Running the smoke tests

cd ${WORKSPACE}
${WORKSPACE}/acceptance.sh \
    --jar=${REPOSITORY}/ontrack-acceptance-${VERSION_FULL}.jar \
    --ontrack-context=production \
    --ontrack-url=https://ontrack.nemerosa.net \
    --ontrack-context=production

