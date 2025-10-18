plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-ui-support"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-repository-support"))
    implementation(project(":ontrack-rabbitmq"))
    implementation(project(":ontrack-extension-queue"))
    implementation(project(":ontrack-extension-recordings"))
    implementation(project(":ontrack-extension-config"))
    implementation("org.springframework:spring-context")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-codec:commons-codec")
    implementation("org.slf4j:slf4j-api")
    implementation("io.micrometer:micrometer-core")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.springframework.boot:spring-boot-starter-mail")


    testImplementation("com.networknt:json-schema-validator")
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-casc")))
    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-extension-config")))

    testImplementation("com.icegreen:greenmail")
    testImplementation("com.icegreen:greenmail-spring")

    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation(project(":ontrack-it-utils"))
    testFixturesImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testFixturesImplementation(testFixtures(project(":ontrack-extension-queue")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
    testRuntimeOnly(project(":ontrack-extension-auto-versioning"))
}
