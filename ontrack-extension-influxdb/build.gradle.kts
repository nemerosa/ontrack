plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))
    api("org.influxdb:influxdb-java")

    implementation("org.slf4j:slf4j-api")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation("org.testcontainers:testcontainers")
    // testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
