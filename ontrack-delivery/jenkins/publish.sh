# Environment

REPOSITORY=/var/lib/jenkins/repository/ontrack/2.0

# Cleanup

rm -rf ${WORKSPACE}/*

# Extract the Docker delivery JAR

unzip ${REPOSITORY}/ontrack-delivery-docker-${ONTRACK_VERSION_FULL}.jar -d ${WORKSPACE}

# Publication of the release

./publish.py \
    --repository=${REPOSITORY} \
    --version-commit=${ONTRACK_VERSION_COMMIT} \
    --version-full=${ONTRACK_VERSION_FULL} \
    --version-release=${ONTRACK_VERSION_DISPLAY} \
    --ontrack-url=https://ontrack.nemerosa.net \
    --github-user=dcoraboeuf \
    --github-token=${GITHUB_TOKEN}
