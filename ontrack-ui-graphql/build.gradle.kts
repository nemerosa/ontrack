plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-ui-support"))
    api(project(":ontrack-tx"))
    api("com.graphql-java:graphql-java")
    api("org.springframework.graphql:spring-graphql")

    implementation(project(":ontrack-job"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.apache.commons:commons-lang3")

    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":ontrack-repository-impl"))
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(":ontrack-extension-ldap"))

    testRuntimeOnly(project(":ontrack-service"))
}

val testJar by tasks.registering(Jar::class) {
    archiveClassifier.set("tests")
    from(sourceSets["test"].output)
}

configure<PublishingExtension> {
    publications {
        maybeCreate<MavenPublication>("mavenCustom").artifact(tasks["testJar"])
    }
}

tasks["assemble"].dependsOn("testJar")

val tests by configurations.creating

artifacts {
    add("tests", testJar)
}
