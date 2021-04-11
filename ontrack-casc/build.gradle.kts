plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-model"))

    implementation(project(":ontrack-repository"))
    implementation(project(":ontrack-extension-api"))
    implementation("org.slf4j:slf4j-api")

    implementation("org.springframework:spring-tx")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))

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
