plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api(project(":ontrack-ui-support"))
    api(project(":ontrack-tx"))
    api("org.springframework.graphql:spring-graphql")

    implementation(project(":ontrack-job"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.apache.commons:commons-lang3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation(testFixtures(project(":ontrack-extension-api")))

    testFixturesImplementation(project(":ontrack-it-utils"))
    testFixturesImplementation("org.jetbrains.kotlin:kotlin-test")

    testRuntimeOnly(project(":ontrack-repository-impl"))
    testRuntimeOnly(project(":ontrack-service"))
}
