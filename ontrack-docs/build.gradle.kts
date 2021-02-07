import org.asciidoctor.gradle.jvm.AsciidoctorPdfTask
import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    groovy
    `java-library`
    id("org.asciidoctor.jvm.convert") version "2.3.0"
    id("org.asciidoctor.jvm.pdf") version "2.3.0"
}

dependencies {
    implementation(project(":ontrack-dsl-v4"))
    implementation(project(":ontrack-json"))
    implementation("commons-io:commons-io")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.codehaus.groovy:groovy")
    implementation("org.codehaus.groovy:groovy-groovydoc")

    runtimeOnly("org.fusesource.jansi:jansi:1.18")
}

if (project.hasProperty("documentation")) {

    asciidoctorj {
        modules {
            diagram.setVersion("1.5.4.1")
        }
    }

    val generateDoc by tasks.registering(JavaExec::class) {
        dependsOn("classes")
        dependsOn(":ontrack-dsl-v4:classes")
        main = "net.nemerosa.ontrack.docs.DSLDocGenerator"
        classpath = sourceSets["main"].runtimeClasspath
        args = listOf(
                project(":ontrack-dsl-v4").file("src/main/groovy").absolutePath,
                "build/dsl"
        )

        inputs.dir(project(":ontrack-dsl-v4").file("src/main/groovy"))
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
