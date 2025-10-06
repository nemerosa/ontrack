plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-extension-auto-versioning-config"))

    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-extension-scm"))
    implementation(project(":ontrack-extension-general"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-repository-support"))
    implementation(project(":ontrack-extension-queue"))
    implementation(project(":ontrack-extension-recordings"))
    implementation(project(":ontrack-rabbitmq"))
    implementation("io.micrometer:micrometer-core")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml") // TODO Remove when removing the Yaml class
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-codec:commons-codec")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation(project(":ontrack-extension-notifications"))
    implementation(project(":ontrack-extension-workflows"))

    testImplementation(project(":ontrack-extension-general"))
    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-casc")))
    testImplementation(testFixtures(project(":ontrack-extension-general")))
    testImplementation(testFixtures(project(":ontrack-extension-notifications")))
    testImplementation(testFixtures(project(":ontrack-extension-scm")))
    // testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    // testImplementation(project(path = ":ontrack-extension-workflows", configuration = "tests"))
    // testImplementation(project(path = ":ontrack-extension-queue", configuration = "tests"))
    // testImplementation(project(path = ":ontrack-extension-recordings", configuration = "tests"))
    testImplementation("com.networknt:json-schema-validator")
    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
