import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

description = "Search based on Elasticsearch"

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))
    api("org.elasticsearch.client:elasticsearch-rest-high-level-client")

    implementation("org.springframework.security:spring-security-core")
    implementation("org.slf4j:slf4j-api")

    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
