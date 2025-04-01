plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-ui-graphql"))

    implementation(project(":ontrack-repository-support"))
    implementation("io.micrometer:micrometer-core")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.opencsv:opencsv")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-ui-support")))
    testImplementation(testFixtures(project(":ontrack-extension-api")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
