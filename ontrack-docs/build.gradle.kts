plugins {
    `java-library`
//    id("org.asciidoctor.jvm.convert") version "4.0.5"
//    id("org.asciidoctor.jvm.pdf") version "4.0.5"
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

// ===== Mkdocs

val venvDir = file(".venv")
val venvPython = file("$venvDir/bin/python")
val venvMkdocs = file("$venvDir/bin/mkdocs")

// Task 1: Create virtual environment
tasks.register<Exec>("createVenv") {
    group = "documentation"
    description = "Create Python virtual environment"

    inputs.file("pyproject.toml")
    outputs.dir(venvDir)

    commandLine("python3", "-m", "venv", ".venv")

    doFirst {
        println("Creating Python virtual environment...")
    }
}

// Task 2: Install dependencies (including mkdocs)
tasks.register<Exec>("installDeps") {
    group = "documentation"
    description = "Install Python dependencies from local setup"

    dependsOn("createVenv")

    // Run only if venv exists and dependencies might be stale
    onlyIf { venvDir.exists() }

    commandLine(venvPython.absolutePath, "-m", "pip", "install", ".")

    // Track input files to detect when reinstall is needed
    inputs.files("pyproject.toml")
        .withPathSensitivity(PathSensitivity.RELATIVE)
        .optional()
    outputs.file("$venvDir/.deps-installed")

    doFirst {
        println("Installing Python dependencies...")
    }

    doLast {
        // Touch marker file to track installation
        file("$venvDir/.deps-installed").writeText(System.currentTimeMillis().toString())
    }
}

tasks.register<Exec>("buildDocs") {
    group = "documentation"
    description = "Build MkDocs documentation"

    mustRunAfter("integrationTest")

    dependsOn("installDeps")

    workingDir = projectDir
    environment("YONTRACK_VERSION", project.version.toString())
    environment("YONTRACK_DOCS_TITLE", "Yontrack ${project.version} documentation")
    commandLine(
        venvMkdocs.absolutePath, "build",
    )

    // Define inputs and outputs for up-to-date checking
    inputs.dir("docs").withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file("mkdocs.yml")
    outputs.dir("$projectDir/site")

    doFirst {
        println("Building MkDocs documentation...")
    }
}

// Task 4: Clean generated documentation
tasks.register<Delete>("cleanDocs") {
    group = "documentation"
    description = "Clean generated documentation and virtual environment"

    delete("$projectDir/site")
    delete(venvDir)
}

tasks.named("clean") {
    dependsOn("cleanDocs")
}

tasks.named("build") {
    dependsOn("buildDocs")
}

// ===== Asciidoc

//asciidoctorj {
//    setVersion("2.5.13")
//    modules {
//        diagram.use()
//    }
//}

// HTML specific settings
//tasks.named<AsciidoctorTask>("asciidoctor") {
//    description = "Generates HTML documentation."
//    attributes = mapOf(
//        "ontrack-version" to version,
//        "spring-boot-version" to (
//                org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
//                    .split(":")
//                    .getOrNull(2)
//                    ?.removeSuffix(")")
//                    ?: "unknown"
//                ),
//        "icons" to "font"
//    )
//    logDocuments = true
//    baseDirFollowsSourceDir()
//    sources {
//        include("index.adoc")
//    }
//}

// PDF specific settings

//tasks.named<AsciidoctorPdfTask>("asciidoctorPdf") {
//    dependsOn("asciidoctor")
//    description = "Generates PDF documentation."
//    attributes = mapOf(
//        "ontrack-version" to version,
//        "spring-boot-version" to (
//                org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
//                    .split(":")
//                    .getOrNull(2)
//                    ?.removeSuffix(")")
//                    ?: "unknown"
//                ),
//        "icons" to "font",
//        "imagesdir" to file("build/docs/asciidoc")
//    )
//    logDocuments = true
//    baseDirFollowsSourceDir()
//    sources {
//        include("index.adoc")
//    }
//}

//tasks.named<Task>("build") {
//    dependsOn("asciidoctor")
//    dependsOn("asciidoctorPdf")
//}
