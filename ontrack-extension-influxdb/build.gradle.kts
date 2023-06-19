import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))
    api("org.influxdb:influxdb-java")

    implementation("org.slf4j:slf4j-api")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation("org.testcontainers:testcontainers")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
