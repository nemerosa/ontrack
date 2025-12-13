plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-ui-support"))

    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-notifications"))
    implementation("org.slf4j:slf4j-api")
    implementation("com.slack.api:slack-api-client")

    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation("com.networknt:json-schema-validator")
    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-extension-casc")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
