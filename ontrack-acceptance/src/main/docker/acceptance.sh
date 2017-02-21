#!/usr/bin/env bash
exec java \
    ${JAVA_OPTIONS} \
    -jar /ontrack/acceptance/app/ontrack-acceptance.jar \
    --ontrack.selenium.url=${ONTRACK_ACCEPTANCE_SELENIUM_URL} \
    --ontrack.url=${ONTRACK_ACCEPTANCE_TARGET_URL} \
    --ontrack.admin=${ONTRACK_ACCEPTANCE_TARGET_ADMIN_PASSWORD} \
    --ontrack.disableSsl=${ONTRACK_ACCEPTANCE_TARGET_SSL_DISABLED} \
    --ontrack.context=${ONTRACK_ACCEPTANCE_CONTEXT} \
    --ontrack.timeout=${ONTRACK_ACCEPTANCE_TIMEOUT} \
    --ontrack.implicitWait=${ONTRACK_ACCEPTANCE_IMPLICIT_WAIT} \
    --ontrack.resultFile=/ontrack/acceptance/results/acceptance-tests.xml
