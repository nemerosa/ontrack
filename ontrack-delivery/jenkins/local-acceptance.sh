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
