plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api(project(":ontrack-extension-issues"))
    api(project(":ontrack-extension-indicators"))

    implementation(project(":ontrack-ui-graphql"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-codec:commons-codec")
    implementation(project(":ontrack-repository-support"))
    implementation("io.micrometer:micrometer-core")
    implementation("com.opencsv:opencsv")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))

    testFixturesImplementation(project(":ontrack-it-utils"))
    testFixturesImplementation(project(":ontrack-test-utils"))
    testFixturesImplementation("org.jetbrains.kotlin:kotlin-test")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
