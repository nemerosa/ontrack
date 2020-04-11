plugins {
    groovy
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-ui-graphql"))
    
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-text")
    implementation("org.slf4j:slf4j-api")

    testImplementation("org.codehaus.groovy:groovy")
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
