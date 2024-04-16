plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-api"))
    api(project(":ontrack-client"))
    api(project(":ontrack-job"))
    api(project(":ontrack-ui-support"))
    api("org.springframework:spring-context")
    api("org.springframework:spring-tx")
    api("org.springframework:spring-web")
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-actuator")
    api("org.springframework.boot:spring-boot-autoconfigure")

    implementation("org.apache.commons:commons-lang3")
    implementation("org.slf4j:slf4j-api")

    // Make sure the following libraries are available for the extension when they need them
    runtimeOnly(project(":ontrack-git"))
    runtimeOnly(project(":ontrack-tx"))
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
