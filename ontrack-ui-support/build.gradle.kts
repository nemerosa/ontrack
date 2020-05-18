plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-model"))
    api(project(":ontrack-extension-api"))
    api("org.springframework:spring-web")
    api("org.springframework:spring-webmvc")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.apache.commons:commons-lang3")

    testApi(project(":ontrack-it-utils"))

    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
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
