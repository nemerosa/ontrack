# Environment

REPOSITORY=/var/lib/jenkins/repository/ontrack/2.0

# Cleanup

rm -rf ${WORKSPACE}/*

# Extract the Docker delivery JAR

unzip ${REPOSITORY}/ontrack-delivery-docker-${VERSION_FULL}.jar -d ${WORKSPACE}

# Publication of the release

./publish.py \
    --repository=${REPOSITORY} \
    --version-commit=${VERSION_COMMIT} \
    --version-full=${VERSION_FULL} \
    --version-release=${VERSION_DISPLAY} \
    --ontrack-url=https://ontrack.nemerosa.net \
    --github-user=dcoraboeuf \
    --github-token=${GITHUB_TOKEN} \
    --ossrh-profile=${OSSRH_PROFILE}
