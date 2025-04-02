plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-extension-queue"))

    implementation("io.micrometer:micrometer-core")
    implementation("org.slf4j:slf4j-api")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-codec:commons-codec")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-recordings"))
    implementation(project(":ontrack-repository-support"))

    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation("com.networknt:json-schema-validator")
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-api")))
    testImplementation(testFixtures(project(":ontrack-extension-casc")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
