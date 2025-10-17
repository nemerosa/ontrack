plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-git"))
    api("org.gitlab4j:gitlab4j-api")

    implementation("org.apache.commons:commons-lang3")
    implementation(project(":ontrack-ui-graphql"))

    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-issues")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
    testRuntimeOnly(project(":ontrack-extension-auto-versioning"))
}
