rootProject.name = "ontrack"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

include(":ontrack-common")
include(":ontrack-json")
include(":ontrack-test-utils")
include(":ontrack-model")
include(":ontrack-client")
include(":ontrack-job")
include(":ontrack-dsl-v4")
include(":ontrack-dsl-shell")
include(":ontrack-git")
include(":ontrack-extension-api")
include(":ontrack-extension-support")
include(":ontrack-extension-plugin")
include(":ontrack-it-utils")
include(":ontrack-database")
include(":ontrack-rabbitmq")
include(":ontrack-repository")
include(":ontrack-repository-impl")
include(":ontrack-repository-support")
include(":ontrack-service")
include(":ontrack-tx")
include(":ontrack-ui")
include(":ontrack-ui-graphql")
include(":ontrack-ui-support")
include(":ontrack-web")
include(":ontrack-web-core")
include(":ontrack-web-tests")
include(":ontrack-acceptance")
include(":ontrack-docs")

// KDSL
include(":ontrack-kdsl")
include(":ontrack-kdsl-acceptance")

// Core extensions
include(":ontrack-extension-ldap")
include(":ontrack-extension-oidc")
include(":ontrack-extension-artifactory")
include(":ontrack-extension-general")
include(":ontrack-extension-issues")
include(":ontrack-extension-combined")
include(":ontrack-extension-jenkins")
include(":ontrack-extension-jira")
include(":ontrack-extension-scm")
include(":ontrack-extension-git")
include(":ontrack-extension-github")
include(":ontrack-extension-gitlab")
include(":ontrack-extension-stash")
include(":ontrack-extension-bitbucket-cloud")
include(":ontrack-extension-stale")
include(":ontrack-extension-vault")
include(":ontrack-extension-influxdb")
include(":ontrack-extension-sonarqube")
include(":ontrack-extension-indicators")
include(":ontrack-extension-casc")
include(":ontrack-extension-elastic")
include(":ontrack-extension-notifications")
include(":ontrack-extension-slack")
include(":ontrack-extension-chart")
include(":ontrack-extension-delivery-metrics")
include(":ontrack-extension-auto-versioning")
include(":ontrack-extension-license")
include(":ontrack-extension-tfc")
include(":ontrack-extension-recordings")
include(":ontrack-extension-hook")
include(":ontrack-extension-queue")
include(":ontrack-extension-workflows")
include(":ontrack-extension-environments")
