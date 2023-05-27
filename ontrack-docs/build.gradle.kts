import org.asciidoctor.gradle.jvm.AsciidoctorPdfTask
import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    id("org.asciidoctor.jvm.convert") version "2.3.0"
    id("org.asciidoctor.jvm.pdf") version "2.3.0"
}

if (project.hasProperty("documentation")) {

    asciidoctorj {
        modules {
            diagram.setVersion("1.5.4.1")
        }
    }

    // HTML specific settings
    tasks.named<AsciidoctorTask>("asciidoctor") {
        description = "Generates HTML documentation."
        attributes = mapOf(
                "ontrack-version" to version,
                "spring-boot-version" to Versions.springBootVersion,
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
        dependsOn("asciidoctor")
        description = "Generates PDF documentation."
        attributes = mapOf(
                "ontrack-version" to version,
                "spring-boot-version" to Versions.springBootVersion,
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

}
