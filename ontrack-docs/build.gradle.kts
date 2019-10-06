import org.asciidoctor.gradle.jvm.AsciidoctorPdfTask
import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    groovy
    id("org.asciidoctor.jvm.convert") version "2.3.0"
    id("org.asciidoctor.jvm.pdf") version "2.3.0"
}

dependencies {
    compile(project(":ontrack-dsl"))
    compile(project(":ontrack-json"))
    compile("commons-io:commons-io")
    compile("org.codehaus.groovy:groovy")
    compile("org.codehaus.groovy:groovy-groovydoc")

    runtime("org.fusesource.jansi:jansi:1.18")
}

if (project.hasProperty("documentation")) {

    val springBootVersion: String by project

    asciidoctorj {
        modules {
            diagram.setVersion("1.5.4.1")
        }
    }

    val generateDoc by tasks.registering(JavaExec::class) {
        dependsOn("classes")
        dependsOn(":ontrack-dsl:classes")
        main = "net.nemerosa.ontrack.docs.DSLDocGenerator"
        classpath = sourceSets["main"].runtimeClasspath
        args = listOf(
                project(":ontrack-dsl").file("src/main/groovy").absolutePath,
                "build/dsl"
        )

        inputs.dir(project(":ontrack-dsl").file("src/main/groovy"))
        outputs.dir("build/dsl")
    }

    val prepareGeneratedDoc by tasks.registering(Copy::class) {
        dependsOn(generateDoc)
        from("build/dsl")
        include("*.adoc")
        into("src/docs/asciidoc/generated")
    }

    // HTML specific settings
    tasks.named<AsciidoctorTask>("asciidoctor") {
        dependsOn(prepareGeneratedDoc)
        description = "Generates HTML documentation."
        attributes = mapOf(
                "ontrack-version" to version,
                "spring-boot-version" to springBootVersion,
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
                "spring-boot-version" to springBootVersion,
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

    rootProject.tasks.named<Zip>("publicationPackage") {
        dependsOn(":ontrack-docs:asciidoctor")
        dependsOn(":ontrack-docs:asciidoctorPdf")
        from(project(":ontrack-docs").file("build/docs/asciidoc")) {
            into("html5")
        }
        from(project(":ontrack-docs").file("build/docs/asciidocPdf")) {
            include("*.pdf")
            into("pdf")
        }
    }
}
