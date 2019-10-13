plugins {
    groovy
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))

    implementation("org.codehaus.groovy:groovy")
    implementation("org.apache.commons:commons-lang3")
    implementation("com.google.guava:guava")
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
