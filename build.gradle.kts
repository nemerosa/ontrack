import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeUp
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import net.nemerosa.ontrack.gradle.GitterAnnouncement
import net.nemerosa.ontrack.gradle.OntrackChangeLog
import net.nemerosa.ontrack.gradle.OntrackLastReleases
import net.nemerosa.ontrack.gradle.RemoteAcceptanceTest
import net.nemerosa.versioning.VersioningExtension
import net.nemerosa.versioning.VersioningPlugin
import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${Versions.kotlinVersion}")
    }
}

// GitHub
val gitHubToken: String by project
val gitHubOwner: String by project
val gitHubRepo: String by project

plugins {
    java
    id("net.nemerosa.versioning") version "2.8.2" apply false
    id("org.sonarqube") version "2.5"
    id("com.avast.gradle.docker-compose") version "0.14.3"
    id("com.bmuschko.docker-remote-api") version "6.4.0"
    id("org.springframework.boot") version Versions.springBootVersion apply false
    id("com.github.breadmoirai.github-release") version "2.2.11"
    // Site
    id("org.ajoberstar.git-publish") version "2.1.1"
}

/**
 * Meta information
 */

apply<VersioningPlugin>()
version = extensions.getByType<VersioningExtension>().info.display

allprojects {
    group = "net.nemerosa.ontrack"
}

subprojects {
    version = rootProject.version
}

/**
 * Resolution
 */

allprojects {
    repositories {
        mavenCentral()
    }
}


/**
 * Integration test environment
 */

val itProject: String by project

// Pre-integration tests: starting Postgresql

configure<ComposeExtension> {
    createNested("integrationTest").apply {
        useComposeFiles = listOf("compose/docker-compose-it.yml")
        projectName = itProject
    }
}

val preIntegrationTest by tasks.registering {
    dependsOn("integrationTestComposeUp")
    // When done
    doLast {
        val host = tasks.named<ComposeUp>("integrationTestComposeUp").get().servicesInfos["db"]?.host!!
        val port = tasks.named<ComposeUp>("integrationTestComposeUp").get().servicesInfos["db"]?.firstContainer?.tcpPort!!
        val url = "jdbc:postgresql://$host:$port/ontrack"
        val jdbcUrl: String by rootProject.extra(url)
        logger.info("Pre integration test JDBC URL = $jdbcUrl")
    }
}

// Post-integration tests: stopping Postgresql

val postIntegrationTest by tasks.registering {
    dependsOn("integrationTestComposeDown")
}

/**
 * Java projects
 */

val javaProjects = subprojects.filter {
    it.path != ":ontrack-web"
}

val exportedProjects = javaProjects.filter {
    it.path != ":ontrack-acceptance"
}

val coreProjects = javaProjects.filter {
    it.path != ":ontrack-dsl-v4"
}

configure(javaProjects) p@{

    /**
     * For all Java projects
     */

    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    // Java level
    java.sourceCompatibility = JavaVersion.VERSION_11

}

configure(exportedProjects) p@ {

    // Documentation

    if (hasProperty("documentation")) {

        // Javadoc

        val javadocJar = tasks.register<Jar>("javadocJar") {
            archiveClassifier.set("javadoc")
            from("javadoc")
        }

        // Sources

        val sourcesJar = tasks.register<Jar>("sourcesJar") {
            dependsOn(JavaPlugin.CLASSES_TASK_NAME)
            archiveClassifier.set("sources")
            from(project.the<SourceSetContainer>()["main"].allSource)
        }

        artifacts {
            add("archives", javadocJar)
            add("archives", sourcesJar)
        }

        // Assembly for Javadoc & Sources
        tasks.named("assemble") {
            dependsOn(javadocJar)
            dependsOn(sourcesJar)
        }

    }

    // POM file

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenCustom") {
                from(components["java"])
                if (hasProperty("documentation")) {
                    artifact(tasks["sourcesJar"])
                    artifact(tasks["javadocJar"])
                }
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
                pom pom@{
                    name.set(this@p.name)
                    this@p.afterEvaluate {
                        val description = if (this.description.isNullOrBlank()) {
                            "Ontrack module: ${this.name}"
                        } else {
                            this.description
                        }
                        this@pom.description.set(description)
                    }
                    url.set("http://nemerosa.github.io/ontrack")
                    licenses {
                        license {
                            name.set("The MIT License (MIT)")
                            url.set("http://opensource.org/licenses/MIT")
                            distribution.set("repo")
                        }
                    }
                    scm {
                        connection.set("scm:git://github.com/nemerosa/ontrack")
                        developerConnection.set("scm:git://github.com/nemerosa/ontrack")
                        url.set("https://github.com/nemerosa/ontrack/")
                    }
                    developers {
                        developer {
                            id.set("dcoraboeuf")
                            name.set("Damien Coraboeuf")
                            email.set("damien.coraboeuf@nemerosa.com")
                        }
                    }
                }
            }
        }
    }

    tasks.named("assemble") {
        dependsOn("generatePomFileForMavenCustomPublication")
    }

    // Signature

    configure<SigningExtension> {
        sign(extensions.getByType(PublishingExtension::class).publications["mavenCustom"])
    }

}

configure(coreProjects) p@{

    /**
     * For all Java projects
     */

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "io.spring.dependency-management")

    configure<AllOpenExtension> {
        annotation("net.nemerosa.ontrack.model.structure.OpenEntity")
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES) {
                bomProperty("kotlin.version", Versions.kotlinVersion)
            }
        }
        dependencies {
            dependency("commons-io:commons-io:2.6")
            dependency("org.apache.commons:commons-text:1.8")
            dependency("net.jodah:failsafe:1.1.1")
            dependency("commons-logging:commons-logging:1.2")
            dependency("org.apache.commons:commons-math3:3.6.1")
            dependency("args4j:args4j:2.33")
            dependency("org.jgrapht:jgrapht-core:1.3.0")
            dependency("com.graphql-java:graphql-java:15.0")
            dependency("com.opencsv:opencsv:5.2")
            dependency("org.testcontainers:testcontainers:1.16.2")
            dependency("org.jetbrains.kotlin:kotlin-test:${Versions.kotlinVersion}")
            // Overrides from Spring Boot
            dependency("org.postgresql:postgresql:42.5.0")
            // JWT
            dependency("io.jsonwebtoken:jjwt-api:${Versions.jjwtVersion}")
            dependency("io.jsonwebtoken:jjwt-impl:${Versions.jjwtVersion}")
            dependency("io.jsonwebtoken:jjwt-jackson:${Versions.jjwtVersion}")
            // Overriding Elastic Search client version
            // See https://docs.spring.io/dependency-management-plugin/docs/current/reference/html/#dependency-management-configuration-bom-import-override-dependency-management
            // and the warning about the POM (explicit overridding is needed here)
            dependency("org.elasticsearch:elasticsearch:${Versions.elasticVersion}")
            dependency("org.elasticsearch.client:elasticsearch-rest-client:${Versions.elasticVersion}")
            dependency("org.elasticsearch.client:elasticsearch-rest-high-level-client:${Versions.elasticVersion}")
        }
    }

    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}")
        "implementation"("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutinesVersion}")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.kotlinCoroutinesVersion}")
        // Lombok
        "compileOnly"("org.projectlombok:lombok:1.18.10")
        "annotationProcessor"("org.projectlombok:lombok:1.18.10")
        "testCompileOnly"("org.projectlombok:lombok:1.18.10")
        "testAnnotationProcessor"("org.projectlombok:lombok:1.18.10")
        // Testing
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.junit.vintage:junit-vintage-engine") {
            exclude(group = "org.hamcrest", module = "hamcrest-core")
        }
        "testImplementation"("org.mockito:mockito-core")
        "testImplementation"("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
        "testImplementation"("io.mockk:mockk:${Versions.mockkVersion}")
        "testImplementation"("io.mockk:mockk-dsl:${Versions.mockkVersion}")
        "testImplementation"("io.mockk:mockk-dsl-jvm:${Versions.mockkVersion}")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    // Unit tests run with the `test` task
    tasks.named<Test>("test") {
        useJUnitPlatform()
        include("**/*Test.class")
        minHeapSize = "512m"
        maxHeapSize = "4096m"
    }

    // Integration tests
    val integrationTest by tasks.registering(Test::class) {
        useJUnitPlatform()
        mustRunAfter("test")
        include("**/*IT.class")
        minHeapSize = "128m"
        maxHeapSize = "3072m"
        dependsOn(":preIntegrationTest")
        finalizedBy(":postIntegrationTest")
        /**
         * Sets the JDBC URL
         */
        doFirst {
            println("Setting JDBC URL for IT: ${rootProject.ext["jdbcUrl"]}")
            systemProperty("spring.datasource.url", rootProject.ext["jdbcUrl"]!!)
            systemProperty("spring.datasource.username", "ontrack")
            systemProperty("spring.datasource.password", "ontrack")
            // Ignoring ES index creation issues at test time (concurrency)
            systemProperty("ontrack.config.search.index.ignore-existing", "true")
        }
    }

    // Synchronization with shutting down the database
    rootProject.tasks.named("integrationTestComposeDown") {
        mustRunAfter(integrationTest)
    }

}

/**
 * Docker tasks
 */

val dockerPrepareEnv by tasks.registering(Copy::class) {
    dependsOn(":ontrack-ui:bootJar")
    from("ontrack-ui/build/libs")
    include("*-app.jar")
    into(project.file("docker"))
    rename(".*", "ontrack.jar")
}

val dockerBuild by tasks.registering(DockerBuildImage::class) {
    dependsOn(dockerPrepareEnv)
    inputDir.set(file("docker"))
    images.add("nemerosa/ontrack:$version")
    images.add("nemerosa/ontrack:latest")
}

/**
 * Acceptance tasks
 */

dockerCompose {
    createNested("local").apply {
        useComposeFiles = listOf("compose/docker-compose-local.yml")
        projectName = "ci"
        captureContainersOutputToFiles = project.file("${project.buildDir}/local-logs")
        tcpPortsToIgnoreWhenWaiting = listOf(8083, 8086)
    }
}

tasks.named<ComposeUp>("localComposeUp") {
    dependsOn(dockerBuild)
    doLast {
        val host = servicesInfos["ontrack"]?.host!!
        val port = servicesInfos["ontrack"]?.firstContainer?.tcpPort!!
        val url = "http://$host:$port"
        val ontrackUrl: String by rootProject.extra(url)
        logger.info("Pre acceptance test Ontrack URL = $ontrackUrl")
    }
}

tasks.register("localAcceptanceTest", RemoteAcceptanceTest::class) {
    acceptanceUrlFn = {
        rootProject.extra["ontrackUrl"] as String
    }
    disableSsl = true
    acceptanceTimeout = 300
    acceptanceImplicitWait = 30
    dependsOn("localComposeUp")
    finalizedBy("localComposeDown")
}

/**
 * Development tasks
 */

val devPostgresName: String by project
val devPostgresPort: String by project

dockerCompose {
    createNested("dev").apply {
        useComposeFiles = listOf("compose/docker-compose-dev.yml")
        projectName = "dev"
        captureContainersOutputToFiles = project.file("${project.buildDir}/dev-logs")
        environment["POSTGRES_NAME"] = devPostgresName
        environment["POSTGRES_PORT"] = devPostgresPort
    }
}

val devStart by tasks.registering {
    dependsOn("devComposeUp")
}

val devStop by tasks.registering {
    dependsOn("devComposeDown")
}

/**
 * Documentation preparation
 */

if (hasProperty("documentation")) {

    val releaseDocCopyHtml by tasks.registering(Copy::class) {
        dependsOn(":ontrack-docs:asciidoctor")
        from("ontrack-docs/build/docs/asciidoc")
        exclude(".asciidoctor")
        into("build/site/release/doc/")
    }

    val releaseDocCopyPdf by tasks.registering(Copy::class) {
        dependsOn(":ontrack-docs:asciidoctorPdf")
        from("ontrack-docs/build/docs/asciidocPdf")
        include("index.pdf")
        into("build/site/release")
    }

    val releaseDocPrepare by tasks.registering {
        dependsOn(releaseDocCopyHtml)
        dependsOn(releaseDocCopyPdf)
    }

    tasks.named("build") {
        dependsOn(releaseDocPrepare)
    }

}

/**
 * GitHub release
 */

val gitHubCommit: String by project

val prepareGitHubRelease by tasks.registering(Copy::class) {
    from("ontrack-docs/build/docs/asciidocPdf") {
        include("index.pdf")
        rename { "ontrack.pdf" }
    }
    from("ontrack-ui/build/libs") {
        include("ontrack-ui-${version}-app.jar")
        rename { "ontrack.jar" }
    }
    from("ontrack-postgresql-migration/build/libs") {
        include("ontrack-postgresql-migration-${version}.jar")
        rename { "ontrack-postgresql-migration.jar" }
    }
    from("ontrack-dsl-shell/build/libs") {
        include("ontrack-dsl-shell-${version}-executable.jar")
        rename { "ontrack-dsl-shell.jar" }
    }
    from("ontrack-ui/build") {
        include("graphql.json")
    }
    into("build/release")
}

val gitHubChangeLogReleaseBranch: String by project
val gitHubChangeLogReleaseBranchFilter: String by project

val githubReleaseChangeLog by tasks.registering(OntrackChangeLog::class) {
    ontrackReleaseBranch = gitHubChangeLogReleaseBranch
    ontrackReleaseFilter = gitHubChangeLogReleaseBranchFilter
}

githubRelease {
    token(gitHubToken)
    owner(gitHubOwner)
    repo(gitHubRepo)
    tagName(version.toString())
    releaseName(version.toString())
    targetCommitish(gitHubCommit)
    overwrite(true)
    releaseAssets(
            "build/release/ontrack.jar",
            "build/release/ontrack-dsl-shell.jar",
            "build/release/ontrack.pdf",
            "build/release/graphql.json"
    )
    body {
        githubReleaseChangeLog.get().changeLog
    }
}

val githubRelease by tasks.named("githubRelease") {
    dependsOn(prepareGitHubRelease)
    dependsOn(githubReleaseChangeLog)
}

/**
 * Release & announcement
 */

val gitterToken: String by project
val gitterRoom: String by project

val gitterAnnouncement by tasks.registering(GitterAnnouncement::class) {
    dependsOn(githubReleaseChangeLog)
    mustRunAfter(githubRelease)
    token = gitterToken
    roomId = gitterRoom
    text = {
        """
        |## Ontrack $version is out
        |
        |${githubReleaseChangeLog.get().changeLog}
        """.trimMargin()
    }
}

val announcements by tasks.registering {
    mustRunAfter(githubRelease)
    dependsOn(gitterAnnouncement)
}

val release by tasks.registering {
    dependsOn(githubRelease)
    dependsOn(announcements)
}

/**
 * Site generation
 *
 * Must be called AFTER the current version has been promoted in Ontrack to the RELEASE promotion level.
 *
 * This means having a Site job in the pipeline, after the Publish one, calling the `site` task.
 */

val siteOntrackLast3Releases by tasks.registering(OntrackLastReleases::class) {
    releaseCount = 2
    releaseBranchPattern = "3\\.[\\d]+"
}

val siteOntrackLast4Releases by tasks.registering(OntrackLastReleases::class) {
    releaseCount = 2
    releaseBranchPattern = "4\\.[\\d]+"
}

val siteOntrackLast5Releases by tasks.registering(OntrackLastReleases::class) {
    releaseCount = 4
    releaseBranchPattern = "5\\.[\\d]+(-rc|-beta)?"
}

val sitePagesDocJs by tasks.registering {
    dependsOn(siteOntrackLast3Releases)
    dependsOn(siteOntrackLast4Releases)
    dependsOn(siteOntrackLast5Releases)
    outputs.file(project.file("ontrack-site/src/main/web/output/assets/web/assets/ontrack/doc.js"))
    doLast {
        val allReleases = siteOntrackLast5Releases.get().releases +
                siteOntrackLast4Releases.get().releases +
                siteOntrackLast3Releases.get().releases
        val allVersions = allReleases.joinToString(",") { "'${it.name}'" }
        project.file("ontrack-site/src/main/web/output/assets/web/assets/ontrack/doc.js").writeText(
                """const releases = [$allVersions];"""
        )
    }
}

configure<GitPublishExtension> {
    repoUri.set(project.properties["ontrackGitHubUri"] as String)
    branch.set(project.properties["ontrackGitHubPages"] as String)
    contents {
        from("ontrack-site/src/main/web/output")
    }
    commitMessage.set("GitHub pages for version $version")
}

tasks.named("gitPublishCopy") {
    dependsOn(sitePagesDocJs)
}

val site by tasks.registering {
    dependsOn("gitPublishPush")
}
