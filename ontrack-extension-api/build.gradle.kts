plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-model"))
    api("org.springframework.security:spring-security-config")
    api("org.springframework.security:spring-security-web")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.apache.commons:commons-lang3")
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
