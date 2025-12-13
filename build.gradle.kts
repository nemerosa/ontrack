import com.avast.gradle.dockercompose.ComposeExtension
import java.io.ByteArrayOutputStream

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.springframework.boot") version "3.5.7" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("com.avast.gradle.docker-compose") version "0.17.12"
    id("com.google.cloud.tools.jib") version "3.5.1" apply false
    id("com.github.node-gradle.node") version "7.1.0" apply false
}

/**
 * Meta information
 */

group = "net.nemerosa.ontrack"

/**
 * Versioning
 */

interface InjectedExecOps {
    @get:Inject val execOps: ExecOperations
}

fun execGit(vararg command: String): String {
    val injected = project.objects.newInstance<InjectedExecOps>()
    val output = ByteArrayOutputStream()
    val result = injected.execOps.exec {
        commandLine(*command)
        standardOutput = output
        isIgnoreExitValue = true
    }

    if (result.exitValue != 0) {
        return ""
    }

    return output.toString().trim()
}

fun getCurrentBranch(): String {
    return execGit("git", "rev-parse", "--abbrev-ref", "HEAD").trim()
}

fun findLatestPatchVersion(baseVersion: String, qualifier: String = ""): Int {
    // Get all tags
    val tags = execGit("git", "tag", "-l").trim()

    if (tags.isEmpty()) {
        return -1 // No tags, will start at 0
    }

    // Build regex pattern based on whether we have a qualifier
    val pattern = if (qualifier.isNotEmpty()) {
        // Match tags like "5.0-alpha.1", "5.0-beta.2"
        Regex("^${Regex.escape(baseVersion)}-${Regex.escape(qualifier)}\\.(\\d+)$")
    } else {
        // Match tags like "5.0.1", "5.0.2"
        Regex("^${Regex.escape(baseVersion)}\\.(\\d+)$")
    }

    val matchingPatches = tags.split("\n")
        .mapNotNull { tag ->
            pattern.matchEntire(tag.trim())?.groupValues?.get(1)?.toIntOrNull()
        }

    return matchingPatches.maxOrNull() ?: -1
}

fun computeMainVersion(): String {
    // Read target version from VERSION file
    val versionFile = file("VERSION")
    if (!versionFile.exists()) {
        throw GradleException("VERSION file not found")
    }

    val versionContent = versionFile.readText().trim()

    // Parse version - supports formats like "5.0", "5.0-alpha", "5.0-beta"
    val versionPattern = Regex("^(\\d+\\.\\d+)(?:-(alpha|beta))?$")
    val matchResult = versionPattern.matchEntire(versionContent)
        ?: throw GradleException("VERSION file should contain version in format X.Y, X.Y-alpha, or X.Y-beta (e.g., 5.0, 5.0-alpha, 5.0-beta)")

    val baseVersion = matchResult.groupValues[1]  // e.g., "5.0"
    val qualifier = matchResult.groupValues[2]     // e.g., "alpha", "beta", or empty

    // Find latest tag matching the pattern
    val latestPatch = findLatestPatchVersion(baseVersion, qualifier)

    // Increment patch
    val newPatch = latestPatch + 1

    // Build final version
    return if (qualifier.isNotEmpty()) {
        "$baseVersion-$qualifier.$newPatch"  // e.g., "5.0-alpha.3"
    } else {
        "$baseVersion.$newPatch"             // e.g., "5.0.3"
    }
}

fun computeReleaseVersion(branchName: String): String {
    // Extract version from branch name (e.g., release/5.0 -> 5.0)
    val versionFromBranch = branchName.removePrefix("release/")

    // Validate format (should be X.Y)
    if (!versionFromBranch.matches(Regex("\\d+\\.\\d+"))) {
        throw GradleException("Release branch should be in format release/X.Y (e.g., release/5.0)")
    }

    // Find latest tag matching the pattern (release branches don't use qualifiers)
    val latestPatch = findLatestPatchVersion(versionFromBranch)

    // Increment patch
    val newPatch = latestPatch + 1

    return "$versionFromBranch.$newPatch"
}

fun computeFeatureVersion(branchName: String): String {
    // Read target version from VERSION file
    val versionFile = file("VERSION")
    if (!versionFile.exists()) {
        throw GradleException("VERSION file not found")
    }

    val targetVersion = versionFile.readText().trim()

    // Get short commit hash
    val commitHash = execGit("git", "rev-parse", "--short", "HEAD").trim()

    // Sanitize branch name (replace invalid characters with -)
    val sanitizedBranch = branchName.replace(Regex("[^a-zA-Z0-9._-]"), "-")

    return "$targetVersion-$sanitizedBranch-$commitHash"
}

fun computeVersion(): String {
    val currentBranch = getCurrentBranch()

    return when {
        currentBranch == "main" -> computeMainVersion()
        currentBranch.startsWith("release/") -> computeReleaseVersion(currentBranch)
        else -> computeFeatureVersion(currentBranch)
    }
}

version = computeVersion()
println("Computed version: $version")

tasks.register("writeVersion") {
    description = "Called by the CI engine to write the version into a file"
    doLast {
        val versionFile = file("build/version.txt")
        versionFile.parentFile.mkdirs()
        versionFile.writeText(version.toString())
        println("Version written to: ${versionFile.absolutePath}")
    }
}

/**
 * Sharing all Spring Boot dependencies
 */

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {

    apply(plugin = "io.spring.dependency-management")

    version = rootProject.version

    val jjwtVersion = "0.12.6"
    val greenMailVersion = "1.6.15"
    val mockkVersion = "1.13.17"

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
        dependencies {
            dependency("commons-io:commons-io:2.18.0")
            dependency("org.jsoup:jsoup:1.19.1")
            dependency("org.apache.commons:commons-math3:3.6.1")
            dependency("org.apache.commons:commons-text:1.13.0")
            dependency("org.jgrapht:jgrapht-core:1.5.2")
            dependency("com.opencsv:opencsv:5.10")
            dependency("com.networknt:json-schema-validator:1.5.5")
            dependency("org.gitlab4j:gitlab4j-api:6.1.0")
            dependency("com.slack.api:slack-api-client:1.38.0")
            dependency("org.springframework.vault:spring-vault-core:3.1.2")

            dependency("io.jsonwebtoken:jjwt-api:$jjwtVersion")
            dependency("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
            dependency("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

            dependency("com.icegreen:greenmail:$greenMailVersion")
            dependency("com.icegreen:greenmail-spring:$greenMailVersion")

            dependency("io.mockk:mockk:${mockkVersion}")
            dependency("io.mockk:mockk-jvm:${mockkVersion}")
            dependency("io.mockk:mockk-dsl:${mockkVersion}")
            dependency("io.mockk:mockk-dsl-jvm:${mockkVersion}")

            // Git repository support TODO Will be removed in V6
            dependency("org.eclipse.jgit:org.eclipse.jgit:6.6.1.202309021850-r")
        }
    }

    // Kotlin Coroutines
    // - 1.8.1 for Spring Boot 3.4.4
    // - 1.9.0 for Apollo (:ontrack-kdsl)
    extra["kotlin-coroutines.version"] = "1.9.0"

}

// ===================================================================================================================
// Docker compose
// ===================================================================================================================

configure<ComposeExtension> {
    createNested("integrationTest").apply {
        useComposeFiles.addAll(listOf("compose/docker-compose-it.yml"))
        setProjectName("it")
    }
    createNested("local").apply {
        useComposeFiles.addAll(listOf("compose/docker-compose-local.yml"))
        setProjectName("local")
    }
}

tasks.named("localComposeUp") {
    dependsOn(":ontrack-ui:jibDockerBuild")
    dependsOn(":ontrack-web-core:dockerBuild")
}

// ===================================================================================================================
// Java projects
// ===================================================================================================================

val javaProjects = subprojects.filter {
    it.path != ":ontrack-web-core"
}

configure(javaProjects) {

    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
        exclude("**/*IT.class")
    }

    val integrationTest by tasks.registering(Test::class) {
        group = "verification"
        description = "Integration tests"
        useJUnitPlatform()

        // Only include classes whose names end with 'IT'
        include("**/*IT.class")
        // Set the test classes directory to be the same as the unit tests
        testClassesDirs = sourceSets["test"].output.classesDirs
        classpath = sourceSets["test"].runtimeClasspath

        shouldRunAfter("test")
        minHeapSize = "128m"
        maxHeapSize = "3072m"
        dependsOn(":integrationTestComposeUp")
        finalizedBy(":integrationTestComposeDown")
    }

    // Synchronization with shutting down the database
    rootProject.tasks.named("integrationTestComposeDown") {
        mustRunAfter(integrationTest)
    }

    // Inclusion in lifecycle
    tasks.check {
        dependsOn(integrationTest)
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        implementation("jakarta.validation:jakarta.validation-api")

        runtimeOnly("org.hibernate.validator:hibernate-validator")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testImplementation("org.junit.vintage:junit-vintage-engine") {
            exclude(group = "org.hamcrest", module = "hamcrest-core")
        }
        testImplementation("io.mockk:mockk")
        testImplementation("io.mockk:mockk-jvm")
        testImplementation("io.mockk:mockk-dsl")
        testImplementation("io.mockk:mockk-dsl-jvm")

        // See https://github.com/junit-team/junit5/issues/4374
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        // Lombok
        compileOnly("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
        annotationProcessor("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
        testCompileOnly("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
        testAnnotationProcessor("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
    }

}
