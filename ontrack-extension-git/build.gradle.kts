plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-scm"))
    api(project(":ontrack-extension-stale"))
    api(project(":ontrack-git"))
    api(project(":ontrack-tx"))
    api(project(":ontrack-json"))

    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-repository-support"))
    implementation("org.springframework:spring-tx")
    implementation("commons-io:commons-io")
    implementation("org.apache.commons:commons-lang3")
    implementation("io.micrometer:micrometer-core")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(testFixtures(project(":ontrack-extension-api")))
    testImplementation(testFixtures(project(":ontrack-extension-issues")))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
    testRuntimeOnly("org.springframework.boot:spring-boot-starter-web")
}
