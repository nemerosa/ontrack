import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

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
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-scm", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation("org.codehaus.groovy:groovy")
    
    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
    testRuntimeOnly("org.springframework.boot:spring-boot-starter-web")
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
