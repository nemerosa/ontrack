#!/usr/bin/env bash
exec java \
    ${JAVA_OPTIONS} \
    -jar /ontrack/acceptance/app/ontrack-acceptance.jar \
    --ontrack.acceptance.url=${ONTRACK_ACCEPTANCE_TARGET_URL} \
    --ontrack.acceptance.selenium-grid-url=${ONTRACK_ACCEPTANCE_SELENIUM_GRID_URL} \
    --ontrack.acceptance.selenium-browser-name=${ONTRACK_ACCEPTANCE_SELENIUM_BROWSER_NAME} \
    --ontrack.acceptance.disable-ssl=${ONTRACK_ACCEPTANCE_TARGET_SSL_DISABLED} \
    --ontrack.acceptance.admin=${ONTRACK_ACCEPTANCE_TARGET_ADMIN_PASSWORD} \
    --ontrack.acceptance.context=${ONTRACK_ACCEPTANCE_CONTEXT} \
    --ontrack.acceptance.timeout=${ONTRACK_ACCEPTANCE_TIMEOUT} \
    --ontrack.acceptance.implicit-wait=${ONTRACK_ACCEPTANCE_IMPLICIT_WAIT} \
    --ontrack.acceptance.output-dir=/ontrack/acceptance/output \
    --ontrack.acceptance.result-file-name=acceptance-tests.xml
