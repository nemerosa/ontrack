plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-ui-support"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(project(":ontrack-extension-queue"))
    implementation(project(":ontrack-extension-notifications"))
    implementation(project(":ontrack-ui-graphql"))
    implementation("org.springframework:spring-context")
    implementation("org.slf4j:slf4j-api")
    implementation("io.micrometer:micrometer-core")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-repository-support"))
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-casc")))
    testImplementation(testFixtures(project(":ontrack-extension-queue")))
    testImplementation(testFixtures(project(":ontrack-extension-notifications")))
    testImplementation("com.networknt:json-schema-validator")
    testImplementation(project(":ontrack-it-utils"))
    testImplementation("com.networknt:json-schema-validator")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
