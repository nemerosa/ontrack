rootProject.name = "ontrack"

// FIXME #762 Remove when using final version of Spring Boot
pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.springframework.boot") {
                useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
            }
        }
    }
}

include(":ontrack-common")
include(":ontrack-json")
include(":ontrack-test-utils")
include(":ontrack-model")
include(":ontrack-client")
include(":ontrack-job")
include(":ontrack-dsl")
include(":ontrack-dsl-shell")
include(":ontrack-git")
include(":ontrack-extension-api")
include(":ontrack-extension-support")
include(":ontrack-extension-plugin")
include(":ontrack-it-utils")
include(":ontrack-database")
include(":ontrack-repository")
include(":ontrack-repository-impl")
include(":ontrack-repository-support")
include(":ontrack-service")
include(":ontrack-tx")
include(":ontrack-ui")
include(":ontrack-ui-graphql")
include(":ontrack-ui-support")
include(":ontrack-web")
include(":ontrack-acceptance")
include(":ontrack-postgresql-migration")
include(":ontrack-docs")

// Core extensions
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
include(":ontrack-extension-ldap")
include(":ontrack-extension-stale")
include(":ontrack-extension-vault")
include(":ontrack-extension-influxdb")
include(":ontrack-extension-sonarqube")
