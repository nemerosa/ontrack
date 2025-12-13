plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-issues"))

    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-notifications"))
    implementation(project(":ontrack-tx"))
    implementation("org.springframework:spring-tx")
    implementation("org.apache.commons:commons-lang3")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-scm"))
    testImplementation(project(":ontrack-extension-recordings"))
    testImplementation("com.networknt:json-schema-validator")
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-casc")))
    testImplementation(testFixtures(project(":ontrack-extension-notifications")))
    testImplementation(testFixtures(project(":ontrack-extension-queue")))
    testImplementation(testFixtures(project(":ontrack-extension-scm")))
    testImplementation(testFixtures(project(":ontrack-extension-support")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}