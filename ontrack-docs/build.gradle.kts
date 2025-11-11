import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask

plugins {
    `java-library`
    id("org.asciidoctor.jvm.convert") version "4.0.5"
    id("org.asciidoctor.jvm.pdf") version "4.0.5"
}

description = "Generation of the Yontrack documentation."

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-ui"))

    // Extensions needed for the docs
    testImplementation(project(":ontrack-extension-notifications"))
    testImplementation(project(":ontrack-extension-workflows"))

    testRuntimeOnly(project(":ontrack-extension-support"))
}

asciidoctorj {
    setVersion("2.5.13")
    modules {
        diagram.use()
    }
}

// HTML specific settings
tasks.named<AsciidoctorTask>("asciidoctor") {
    dependsOn("integrationTest")
    description = "Generates HTML documentation."
    attributes = mapOf(
        "ontrack-version" to version,
        // TODO "spring-boot-version" to Versions.springBootVersion,
        "icons" to "font"
    )
    logDocuments = true
    baseDirFollowsSourceDir()
    sources {
        include("index.adoc")
    }
}

// PDF specific settings

tasks.named<AsciidoctorPdfTask>("asciidoctorPdf") {
    dependsOn("integrationTest")
    dependsOn("asciidoctor")
    description = "Generates PDF documentation."
    attributes = mapOf(
        "ontrack-version" to version,
        // TODO "spring-boot-version" to Versions.springBootVersion,
        "icons" to "font",
        "imagesdir" to file("build/docs/asciidoc")
    )
    logDocuments = true
    baseDirFollowsSourceDir()
    sources {
        include("index.adoc")
    }
}

tasks.named<Task>("build") {
    dependsOn("asciidoctor")
    dependsOn("asciidoctorPdf")
}
