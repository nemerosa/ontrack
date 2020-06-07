plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-json"))
    api(project(":ontrack-common"))
    api("javax.validation:validation-api")

    implementation(project(":ontrack-job"))
    implementation("org.apache.commons:commons-text")
    implementation("org.springframework:spring-context")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.slf4j:slf4j-api")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation(project(":ontrack-test-utils"))
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
