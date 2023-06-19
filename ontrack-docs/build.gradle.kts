import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask

plugins {
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("org.asciidoctor.jvm.pdf") version "3.3.2"
}

if (project.hasProperty("documentation")) {

    asciidoctorj {
        modules {
            diagram.use()
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
