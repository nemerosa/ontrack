import com.avast.gradle.dockercompose.ComposeExtension

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.springframework.boot") version "3.4.4" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("net.nemerosa.versioning") version "3.1.0"
    id("com.avast.gradle.docker-compose") version "0.17.12"
    id("com.google.cloud.tools.jib") version "3.4.4" apply false
    id("com.bmuschko.docker-remote-api") version "9.4.0" apply false
}

/**
 * Meta information
 */

group = "net.nemerosa.ontrack"
version = versioning.info.full

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

            // Git repository support TODO Will be removed in V5
            dependency("org.eclipse.jgit:org.eclipse.jgit:6.6.1.202309021850-r")

            // Log JSON TODO Will be removed in V5
            dependency("net.logstash.logback:logstash-logback-encoder:7.3")
        }
    }

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

    val mockkVersion = "1.13.17"

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
        testImplementation("io.mockk:mockk:${mockkVersion}")
        testImplementation("io.mockk:mockk-jvm:${mockkVersion}")
        testImplementation("io.mockk:mockk-dsl:${mockkVersion}")
        testImplementation("io.mockk:mockk-dsl-jvm:${mockkVersion}")

        // Lombok
        compileOnly("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
        annotationProcessor("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
        testCompileOnly("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
        testAnnotationProcessor("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
    }

}

// ===================================================================================================================
// ===================================================================================================================
// ===================================================================================================================
// ===================================================================================================================
// ===================================================================================================================

// OLD BUILD

// ===================================================================================================================
// ===================================================================================================================
// ===================================================================================================================
// ===================================================================================================================
// ===================================================================================================================

//import com.avast.gradle.dockercompose.ComposeExtension
//import com.avast.gradle.dockercompose.tasks.ComposeUp
//import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
//import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
//import net.nemerosa.ontrack.gradle.OntrackChangeLog
//import net.nemerosa.ontrack.gradle.OntrackLastReleases
//import net.nemerosa.versioning.VersioningExtension
//import net.nemerosa.versioning.VersioningPlugin
//import org.ajoberstar.gradle.git.publish.GitPublishExtension
//import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//import org.springframework.boot.gradle.plugin.SpringBootPlugin
//
//// GitHub
//val gitHubToken: String by project
//val gitHubOwner: String by project
//val gitHubRepo: String by project
//
//plugins {
//    java
//    id("net.nemerosa.versioning") version "3.0.0" apply false
//    id("org.sonarqube") version "2.5"
//    id("com.avast.gradle.docker-compose") version "0.16.12"
//    id("com.bmuschko.docker-remote-api") version "9.3.1"
//    id("org.springframework.boot") version Versions.springBootVersion apply false
//    id("io.freefair.aggregate-javadoc") version "4.1.2"
//    id("com.github.breadmoirai.github-release") version "2.2.11"
//    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
//    // Site
//    id("org.ajoberstar.git-publish") version "2.1.1"
//}
//
///**
// * Meta information
// */
//
//apply<VersioningPlugin>()
//version = extensions.getByType<VersioningExtension>().info.display
//
//allprojects {
//    group = "net.nemerosa.ontrack"
//}
//
//subprojects {
//    version = rootProject.version
//}
//
///**
// * Resolution
// */
//
//allprojects {
//    repositories {
//        mavenCentral()
//        // For auto-versioning module, for 4koma (TOML)
//        maven {
//            url = uri("https://jitpack.io")
//        }
//    }
//}
//
//
///**
// * Integration test environment
// */
//
//val itProject: String by project
//
//// Pre-integration tests: starting Postgresql
//
//configure<ComposeExtension> {
//    createNested("integrationTest").apply {
//        useComposeFiles.addAll(listOf("compose/docker-compose-it.yml"))
//        setProjectName(itProject)
//    }
//}
//
//val preIntegrationTest by tasks.registering {
//    dependsOn("integrationTestComposeUp")
//    // When done
//    doLast {
//        val host = tasks.named<ComposeUp>("integrationTestComposeUp").get().servicesInfos["db"]?.host!!
//        val port = tasks.named<ComposeUp>("integrationTestComposeUp").get().servicesInfos["db"]?.firstContainer?.tcpPort!!
//        val url = "jdbc:postgresql://$host:$port/ontrack"
//        val jdbcUrl: String by rootProject.extra(url)
//        logger.info("Pre integration test JDBC URL = $jdbcUrl")
//    }
//}
//
//// Post-integration tests: stopping Postgresql
//
//val postIntegrationTest by tasks.registering {
//    dependsOn("integrationTestComposeDown")
//}
//
///**
// * Java projects
// */
//
//val javaProjects = subprojects.filter {
//    it.path != ":ontrack-web" &&
//            it.path != ":ontrack-web-core"
//}
//
//val exportedProjects = javaProjects
//
//val coreProjects = javaProjects
//
//configure(javaProjects) p@{
//
//    /**
//     * For all Java projects
//     */
//
//    apply(plugin = "java")
//    apply(plugin = "maven-publish")
//    apply(plugin = "signing")
//
//    // Java level
//    java.sourceCompatibility = JavaVersion.VERSION_17
//    java.targetCompatibility = JavaVersion.VERSION_17
//
//}
//
//configure(exportedProjects) p@ {
//
//    // Documentation
//
//    if (hasProperty("documentation")) {
//
//        // Javadoc
//
//        val javadocJar = tasks.register<Jar>("javadocJar") {
//            archiveClassifier.set("javadoc")
//            from("javadoc")
//        }
//
//        // Sources
//
//        val sourcesJar = tasks.register<Jar>("sourcesJar") {
//            dependsOn(JavaPlugin.CLASSES_TASK_NAME)
//            archiveClassifier.set("sources")
//            from(project.the<SourceSetContainer>()["main"].allSource)
//        }
//
//        artifacts {
//            add("archives", javadocJar)
//            add("archives", sourcesJar)
//        }
//
//        // Assembly for Javadoc & Sources
//        tasks.named("assemble") {
//            dependsOn(javadocJar)
//            dependsOn(sourcesJar)
//        }
//
//    }
//
//    // POM file
//
//    configure<PublishingExtension> {
//        publications {
//            create<MavenPublication>("mavenCustom") {
//                from(components["java"])
//                if (hasProperty("documentation")) {
//                    artifact(tasks["sourcesJar"])
//                    artifact(tasks["javadocJar"])
//                }
//                versionMapping {
//                    usage("java-api") {
//                        fromResolutionOf("runtimeClasspath")
//                    }
//                    usage("java-runtime") {
//                        fromResolutionResult()
//                    }
//                }
//                pom pom@{
//                    name.set(this@p.name)
//                    this@p.afterEvaluate {
//                        val description = if (this.description.isNullOrBlank()) {
//                            "Ontrack module: ${this.name}"
//                        } else {
//                            this.description
//                        }
//                        this@pom.description.set(description)
//                    }
//                    url.set("http://nemerosa.github.io/ontrack")
//                    licenses {
//                        license {
//                            name.set("The MIT License (MIT)")
//                            url.set("http://opensource.org/licenses/MIT")
//                            distribution.set("repo")
//                        }
//                    }
//                    scm {
//                        connection.set("scm:git://github.com/nemerosa/ontrack")
//                        developerConnection.set("scm:git://github.com/nemerosa/ontrack")
//                        url.set("https://github.com/nemerosa/ontrack/")
//                    }
//                    developers {
//                        developer {
//                            id.set("dcoraboeuf")
//                            name.set("Damien Coraboeuf")
//                            email.set("damien.coraboeuf@nemerosa.com")
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    tasks.named("assemble") {
//        dependsOn("generatePomFileForMavenCustomPublication")
//    }
//
//    // Signature
//
//    configure<SigningExtension> {
//        sign(extensions.getByType(PublishingExtension::class).publications["mavenCustom"])
//    }
//
//}
//
//val greenMailVersion: String by project
//
//configure(coreProjects) p@{
//
//    /**
//     * For all Java projects
//     */
//
//    apply(plugin = "java")
//    apply(plugin = "kotlin")
//    apply(plugin = "kotlin-spring")
//    apply(plugin = "io.spring.dependency-management")
//
//    configure<AllOpenExtension> {
//        annotation("net.nemerosa.ontrack.model.structure.OpenEntity")
//    }
//
//    ext["spring-security.version"] = "5.8.16"
//
//    configure<DependencyManagementExtension> {
//        imports {
//            mavenBom(SpringBootPlugin.BOM_COORDINATES) {
//                bomProperty("jakarta-json.version", "2.1.2")
//                bomProperty("kotlin.version", Versions.kotlinVersion)
//                bomProperty("kotlin-coroutines.version", Versions.kotlinCoroutinesVersion)
//            }
//        }
//        dependencies {
//            dependency("commons-io:commons-io:2.18.0")
//            dependency("org.apache.commons:commons-text:1.9") // Compatible with org.apache.commons:commons-lang3:3.12.0 provided by Spring Boot
//            // TODO Check dependency("net.jodah:failsafe:1.1.1")
//            // TODO Check dependency("commons-logging:commons-logging:1.2")
//            dependency("org.apache.commons:commons-math3:3.6.1")
//            dependency("args4j:args4j:2.37")
//            dependency("org.jgrapht:jgrapht-core:1.5.2")
//            dependency("com.opencsv:opencsv:5.10")
//            dependency("org.testcontainers:testcontainers:1.20.6")
//            dependency("org.jetbrains.kotlin:kotlin-test:${Versions.kotlinVersion}")
//            dependency("com.networknt:json-schema-validator:1.5.5")
//            // JWT
//            dependency("io.jsonwebtoken:jjwt-api:${Versions.jjwtVersion}")
//            dependency("io.jsonwebtoken:jjwt-impl:${Versions.jjwtVersion}")
//            dependency("io.jsonwebtoken:jjwt-jackson:${Versions.jjwtVersion}")
//            // Spring Boot brings jakarta-json-api version 1.1.6
//            // and Elastic Search 7.17.15. But this one relies on jakarta-json-api version 2.1.2
//            dependency("jakarta.json:jakarta.json-api:2.1.2")
//            // Used for safe HTML
//            dependency("org.jsoup:jsoup:1.19.1")
//            // For TOML (see also #1156)
//            dependency("cc.ekblad:4koma:1.2.0")
//            // JSON schema suppport
//            dependency("com.networknt:json-schema-validator:1.5.5")
//            // SCM support
//            dependency("org.gitlab4j:gitlab4j-api:5.8.0")
//            // LDAP support
//            dependency("com.unboundid:unboundid-ldapsdk:7.0.2")
//            // Testing mail
//            dependency("com.icegreen:greenmail:$greenMailVersion")
//            dependency("com.icegreen:greenmail-spring:$greenMailVersion")
//            // Vault support
//            dependency("org.springframework.vault:spring-vault-core:2.3.4")
//            // Git repository support TODO Will be removed in V5
//            dependency("org.eclipse.jgit:org.eclipse.jgit:6.6.1.202309021850-r")
//            // Log JSON
//            dependency("net.logstash.logback:logstash-logback-encoder:7.3")
//        }
//    }
//
//    dependencies {
//        implementation("jakarta.validation:jakarta.validation-api")
//        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}")
//        "implementation"("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
//        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutinesVersion}")
//        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk9:${Versions.kotlinCoroutinesVersion}")
//        // Lombok
//        "compileOnly"("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
//        "annotationProcessor"("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
//        "testCompileOnly"("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
//        "testAnnotationProcessor"("org.projectlombok:lombok:1.18.26") // TODO V5 Remove dependency on Lombok
//        // Testing
//        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
//        "testImplementation"("org.junit.vintage:junit-vintage-engine") {
//            exclude(group = "org.hamcrest", module = "hamcrest-core")
//        }
//        "testImplementation"("org.mockito:mockito-core") // TODO V5 Remove dependency on Mockito
//        "testImplementation"("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0") // TODO V5 Remove dependency on Mockito
//        "testImplementation"("io.mockk:mockk:${Versions.mockkVersion}")
//        "testImplementation"("io.mockk:mockk-dsl:${Versions.mockkVersion}")
//        "testImplementation"("io.mockk:mockk-dsl-jvm:${Versions.mockkVersion}")
//        "testImplementation"("org.jetbrains.kotlin:kotlin-test")
//    }
//
//    tasks.withType<KotlinCompile> {
//        kotlinOptions {
//            jvmTarget = "17"
//            freeCompilerArgs = listOf("-Xjsr305=strict")
//        }
//    }
//
//    // Unit tests run with the `test` task
//    tasks.named<Test>("test") {
//        useJUnitPlatform()
//        include("**/*Test.class")
//        minHeapSize = "512m"
//        maxHeapSize = "4096m"
//    }
//
//    // Integration tests
//    val integrationTest by tasks.registering(Test::class) {
//        useJUnitPlatform()
//        mustRunAfter("test")
//        include("**/*IT.class")
//        minHeapSize = "128m"
//        maxHeapSize = "3072m"
//        dependsOn(":preIntegrationTest")
//        finalizedBy(":postIntegrationTest")
//        /**
//         * Sets the JDBC URL
//         */
//        doFirst {
//            println("Setting JDBC URL for IT: ${rootProject.ext["jdbcUrl"]}")
//            systemProperty("spring.datasource.url", rootProject.ext["jdbcUrl"]!!)
//            systemProperty("spring.datasource.username", "ontrack")
//            systemProperty("spring.datasource.password", "ontrack")
//            // Ignoring ES index creation issues at test time (concurrency)
//            systemProperty("ontrack.config.search.index.ignore-existing", "true")
//        }
//    }
//
//    // Synchronization with shutting down the database
//    rootProject.tasks.named("integrationTestComposeDown") {
//        mustRunAfter(integrationTest)
//    }
//
//}
//
///**
// * Global Javadoc
// */
//
//tasks.named<Javadoc>("aggregateJavadoc") {
//    include("net/nemerosa/**")
//}
//
//if (project.hasProperty("documentation")) {
//
//    rootProject.tasks.register("javadocPackage", Zip::class) {
//        archiveClassifier.set("javadoc")
//        archiveFileName.set("ontrack-javadoc.zip")
//        dependsOn("aggregateJavadoc")
//        from(rootProject.file("${rootProject.buildDir}/docs/javadoc")) {
//            include("**")
//        }
//    }
//}
//
///**
// * Docker tasks
// */
//
//val dockerPrepareEnv by tasks.registering(Copy::class) {
//    dependsOn(":ontrack-ui:bootJar")
//    from("ontrack-ui/build/libs")
//    include("ontrack-ui-${version}-app.jar")
//    into(project.file("docker"))
//    rename(".*", "ontrack.jar")
//}
//
//val dockerBuild by tasks.registering(DockerBuildImage::class) {
//    dependsOn(dockerPrepareEnv)
//    inputDir.set(file("docker"))
//    images.add("nemerosa/ontrack:$version")
//    images.add("nemerosa/ontrack:latest")
//}
//
///**
// * Launching Ontrack
// */
//
//dockerCompose {
//    createNested("local").apply {
//        useComposeFiles.set(listOf("compose/docker-compose-local.yml"))
//        setProjectName("ci")
//        captureContainersOutputToFiles.set(project.file("${project.buildDir}/local-logs"))
//        tcpPortsToIgnoreWhenWaiting.set(listOf(8083, 8086))
//    }
//}
//
//tasks.named<ComposeUp>("localComposeUp") {
//    dependsOn(dockerBuild)
//    dependsOn(":ontrack-web-core:dockerBuild")
//    doLast {
//        val host = servicesInfos["ontrack"]?.host!!
//        val port = servicesInfos["ontrack"]?.firstContainer?.tcpPort!!
//        val url = "http://$host:$port"
//        val ontrackUrl: String by rootProject.extra(url)
//    }
//}
//
///**
// * Development tasks
// */
//
//val devPostgresName: String by project
//val devPostgresPort: String by project
//
//dockerCompose {
//    createNested("dev").apply {
//        useComposeFiles.set(listOf("compose/docker-compose-dev.yml"))
//        setProjectName("dev")
//        captureContainersOutputToFiles.set(project.file("${project.buildDir}/dev-logs"))
//        environment.put("POSTGRES_NAME", devPostgresName)
//        environment.put("POSTGRES_PORT", devPostgresPort)
//    }
//}
//
//val devStart by tasks.registering {
//    dependsOn("devComposeUp")
//}
//
//val devStop by tasks.registering {
//    dependsOn("devComposeDown")
//}
//
///**
// * Documentation preparation
// */
//
//if (hasProperty("documentation")) {
//
//    val releaseDocCopyHtml by tasks.registering(Copy::class) {
//        dependsOn(":ontrack-docs:asciidoctor")
//        from("ontrack-docs/build/docs/asciidoc")
//        exclude(".asciidoctor")
//        into("build/site/release/doc/")
//    }
//
//    val releaseDocCopyPdf by tasks.registering(Copy::class) {
//        dependsOn(":ontrack-docs:asciidoctorPdf")
//        from("ontrack-docs/build/docs/asciidocPdf")
//        include("index.pdf")
//        into("build/site/release")
//    }
//
//    val releaseDocCopyJavadoc by tasks.registering(Copy::class) {
//        dependsOn("javadocPackage")
//        from("build/distributions") {
//            include("ontrack-javadoc.zip")
//        }
//        into("build/site/release/")
//    }
//
//    val releaseDocPrepare by tasks.registering {
//        dependsOn(releaseDocCopyHtml)
//        dependsOn(releaseDocCopyPdf)
//        dependsOn(releaseDocCopyJavadoc)
//    }
//
//    tasks.named("build") {
//        dependsOn(releaseDocPrepare)
//    }
//
//}
//
///**
// * GitHub release
// */
//
//val gitHubCommit: String by project
//
//val prepareGitHubRelease by tasks.registering(Copy::class) {
//    from("ontrack-docs/build/docs/asciidocPdf") {
//        include("index.pdf")
//        rename { "ontrack.pdf" }
//    }
//    from("ontrack-ui/build/libs") {
//        include("ontrack-ui-${version}-app.jar")
//        rename { "ontrack.jar" }
//    }
//    from("ontrack-ui/build") {
//        include("graphql.json")
//    }
//    into("build/release")
//}
//
//val gitHubChangeLogReleaseBranch: String by project
//val gitHubChangeLogCurrentBuild: String by project
//
//val githubReleaseChangeLog by tasks.registering(OntrackChangeLog::class) {
//    ontrackReleaseBranch = gitHubChangeLogReleaseBranch
//    ontrackCurrentBuild = gitHubChangeLogCurrentBuild
//    format = "text"
//}
//
//githubRelease {
//    token(gitHubToken)
//    owner(gitHubOwner)
//    repo(gitHubRepo)
//    tagName(version.toString())
//    releaseName(version.toString())
//    targetCommitish(gitHubCommit)
//    overwrite(true)
//    releaseAssets(
//            "build/release/ontrack.jar",
//            "build/release/ontrack.pdf",
//            "build/release/graphql.json"
//    )
//    body {
//        githubReleaseChangeLog.get().changeLog
//    }
//}
//
//val githubRelease by tasks.named("githubRelease") {
//    dependsOn(prepareGitHubRelease)
//    dependsOn(githubReleaseChangeLog)
//}
//
///**
// * Release & announcement
// */
//
//val slackReleaseChangeLog by tasks.registering(OntrackChangeLog::class) {
//    ontrackReleaseBranch = gitHubChangeLogReleaseBranch
//    ontrackCurrentBuild = gitHubChangeLogCurrentBuild
//    format = "slack"
//}
//
//val slackMessagePreparation by tasks.registering {
//    dependsOn(slackReleaseChangeLog)
//    mustRunAfter(githubRelease)
//    doLast {
//        val text = """
//            |:ontrack: *Ontrack `$version` is out*
//            |
//            |${slackReleaseChangeLog.get().changeLog}
//            """.trimMargin()
//        project.file("build/slack.txt").writeText(text)
//    }
//}
//
//val announcements by tasks.registering {
//    mustRunAfter(githubRelease)
//    dependsOn(slackMessagePreparation)
//}
//
//val release by tasks.registering {
//    dependsOn(githubRelease)
//    dependsOn(announcements)
//}
//
///**
// * Site generation
// *
// * Must be called AFTER the current version has been promoted in Ontrack to the RELEASE promotion level.
// *
// * This means having a Site job in the pipeline, after the Publish one, calling the `site` task.
// */
//
//val siteOntrackLast3Releases by tasks.registering(OntrackLastReleases::class) {
//    releaseCount = 1
//    releaseBranchPattern = "3\\.\\d+"
//}
//
//val siteOntrackLast4Releases by tasks.registering(OntrackLastReleases::class) {
//    releaseCount = 2
//    releaseBranchPattern = "4\\.\\d+"
//}
//
//val sitePagesDocJs by tasks.registering {
//    dependsOn(siteOntrackLast3Releases)
//    dependsOn(siteOntrackLast4Releases)
//    outputs.file(project.file("ontrack-site/src/main/web/output/assets/web/assets/ontrack/doc.js"))
//    doLast {
//        val allReleases = siteOntrackLast4Releases.get().releases +
//                siteOntrackLast3Releases.get().releases
//        val allVersions = allReleases.joinToString(",") { "'$it'" }
//        project.file("ontrack-site/src/main/web/output/assets/web/assets/ontrack/doc.js").writeText(
//                """const releases = [$allVersions];"""
//        )
//    }
//}
//
//configure<GitPublishExtension> {
//    repoUri.set(project.properties["ontrackGitHubUri"] as String)
//    branch.set(project.properties["ontrackGitHubPages"] as String)
//    contents {
//        from("ontrack-site/src/main/web/output")
//    }
//    commitMessage.set("GitHub pages for version $version")
//}
//
//tasks.named("gitPublishCopy") {
//    dependsOn(sitePagesDocJs)
//}
//
//val site by tasks.registering {
//    dependsOn("gitPublishPush")
//}
