import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-extension-scm"))
    implementation(project(":ontrack-extension-general"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-repository-support"))
    implementation(project(":ontrack-rabbitmq"))
    implementation("io.micrometer:micrometer-core")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml") // TODO Remove when removing the Yaml class
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.commons:commons-lang3")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation(project(":ontrack-extension-notifications"))

    implementation("cc.ekblad:4koma:1.2.0")

    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-scm", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-general", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-notifications", configuration = "tests"))
    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
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

