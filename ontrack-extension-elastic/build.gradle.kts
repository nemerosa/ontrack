import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("co.elastic.clients:elasticsearch-java:${Versions.elasticVersion}")
    implementation("jakarta.json:jakarta.json-api:2.1.2")
    implementation("io.micrometer:micrometer-core")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation("org.testcontainers:testcontainers")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
