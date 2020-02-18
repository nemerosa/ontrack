import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeUp
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.netflix.gradle.plugins.deb.Deb
import com.netflix.gradle.plugins.packaging.SystemPackagingTask
import com.netflix.gradle.plugins.rpm.Rpm
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import net.nemerosa.ontrack.gradle.OntrackChangeLog
import net.nemerosa.ontrack.gradle.OntrackLastReleases
import net.nemerosa.ontrack.gradle.RemoteAcceptanceTest
import net.nemerosa.versioning.VersioningExtension
import net.nemerosa.versioning.VersioningPlugin
import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.redline_rpm.header.Os
import org.springframework.boot.gradle.plugin.SpringBootPlugin

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        val kotlinVersion: String by project
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
    }
}

// FIXME Try to use version in gradle.properties
// val springBootVersion: String by project
val kotlinVersion: String by project
val elasticSearchVersion: String by project

// GitHub
val gitHubUser: String by project
val gitHubToken: String by project
val gitHubOwner: String by project
val gitHubRepo: String by project

extra["elasticsearch.version"] = elasticSearchVersion

plugins {
    java
    jacoco
    id("net.nemerosa.versioning") version "2.8.2" apply false
    id("nebula.deb") version "8.1.0"
    id("nebula.rpm") version "8.1.0"
    id("org.sonarqube") version "2.5"
    id("com.avast.gradle.docker-compose") version "0.9.5"
    id("com.bmuschko.docker-remote-api") version "4.1.0"
    id("org.springframework.boot") version "2.1.9.RELEASE" apply false
    id("io.freefair.aggregate-javadoc") version "4.1.2"
    id("com.github.breadmoirai.github-release") version "2.2.10"
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
        jcenter()
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
        logger.info("Pre integration test JDBC URL = ${jdbcUrl}")
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

val coreProjects = javaProjects.filter {
    it.path != ":ontrack-dsl"
}

configure(javaProjects) p@{

    /**
     * For all Java projects
     */

    apply(plugin = "java")
    apply(plugin = "maven-publish")

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
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/${gitHubOwner}/${gitHubRepo}")
                    credentials {
                        username = gitHubUser
                        password = gitHubToken
                    }
                }
            }
        }
    }

    tasks.named("assemble") {
        dependsOn("generatePomFileForMavenCustomPublication")
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

    configure<DependencyManagementExtension> {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES) {
                bomProperty("kotlin.version", kotlinVersion)
                bomProperty("elasticsearch.version", elasticSearchVersion)
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
            dependency("org.kohsuke:groovy-sandbox:1.19")
            dependency("com.graphql-java:graphql-java:11.0")
            dependency("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
            // Overrides from Spring Boot
            dependency("org.postgresql:postgresql:9.4.1208")
        }
    }

    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
        "implementation"("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
        // Lombok
        "compileOnly"("org.projectlombok:lombok:1.18.10")
        "annotationProcessor"("org.projectlombok:lombok:1.18.10")
        "testCompileOnly"("org.projectlombok:lombok:1.18.10")
        "testAnnotationProcessor"("org.projectlombok:lombok:1.18.10")
        // Testing
        "testImplementation"("junit:junit")
        "testImplementation"("org.mockito:mockito-core")
        "testImplementation"("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    // Unit tests run with the `test` task
    tasks.named<Test>("test") {
        include("**/*Test.class")
    }

    // Integration tests
    val integrationTest by tasks.registering(Test::class) {
        mustRunAfter("test")
        include("**/*IT.class")
        minHeapSize = "512m"
        maxHeapSize = "1024m"
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
        }
    }

    // Synchronization with shutting down the database
    rootProject.tasks.named("integrationTestComposeDown") {
        mustRunAfter(integrationTest)
    }

}

/**
 * Code coverage report
 */

configure(coreProjects) {
    apply(plugin = "jacoco")
}

val codeCoverageReport by tasks.registering(JacocoReport::class) {
    executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

    javaProjects.forEach {
        sourceSets(it.sourceSets["main"])
    }

    reports {
        xml.isEnabled = true
        xml.destination = file("${buildDir}/reports/jacoco/report.xml")
        html.isEnabled = false
        csv.isEnabled = false
    }
}

configure(javaProjects) {
    tasks.named("test") test@{
        codeCoverageReport {
            dependsOn(this@test)
        }
    }
    val integrationTest = tasks.findByName("integrationTest")
    if (integrationTest != null) {
        codeCoverageReport {
            dependsOn(integrationTest)
        }
    }
}

val jacocoExecFile: String by project
val jacocoReportFile: String by project

val codeDockerCoverageReport by tasks.registering(JacocoReport::class) {
    executionData(fileTree(project.rootDir.absolutePath).include(jacocoExecFile))

    javaProjects.forEach {
        sourceSets(it.sourceSets["main"])
    }

    reports {
        xml.isEnabled = true
        xml.destination = file(jacocoReportFile)
        html.isEnabled = false
        csv.isEnabled = false
    }
}

/**
 * Global Javadoc
 */

tasks.named<Javadoc>("aggregateJavadoc") {
    include("net/nemerosa/**")
}

if (project.hasProperty("documentation")) {

    rootProject.tasks.register("javadocPackage", Zip::class) {
        archiveClassifier.set("javadoc")
        archiveFileName.set("ontrack-javadoc.zip")
        dependsOn("aggregateJavadoc")
        from(rootProject.file("$rootProject.buildDir/docs/javadoc"))
    }
}

/**
 * Packaging for OS
 *
 * The package version does not accept versions like the ones generated
 * from the Versioning plugin for the feature branches for example.
 */

val packageVersion: String = if (version.toString().matches("\\d+\\.\\d+\\.\\d+".toRegex())) {
    version.toString().replace("[^0-9\\.-_]".toRegex(), "")
} else {
    "0.0.0"
}
println("Using package version = $packageVersion")

val debPackage by tasks.registering(Deb::class) {
    dependsOn(":ontrack-ui:bootJar")

    link("/etc/init.d/ontrack", "/opt/ontrack/bin/ontrack.sh")
}

val rpmPackage by tasks.registering(Rpm::class) {
    dependsOn(":ontrack-ui:bootJar")

    user = "ontrack"
    link("/etc/init.d/ontrack", "/opt/ontrack/bin/ontrack.sh")
}

tasks.withType(SystemPackagingTask::class) {

    packageName = "ontrack"
    release = "1"
    version = packageVersion
    os = Os.LINUX // only applied to RPM

    preInstall(file("gradle/os-package/preInstall.sh"))
    postInstall(file("gradle/os-package/postInstall.sh"))

    from(project(":ontrack-ui").file("build/libs"), closureOf<CopySpec> {
        include("ontrack-ui-${project.version}.jar")
        into("/opt/ontrack/lib")
        rename(".*", "ontrack.jar")
    })

    from("gradle/os-package", closureOf<CopySpec> {
        include("ontrack.sh")
        into("/opt/ontrack/bin")
        fileMode = 0x168 // 0550
    })
}

val osPackages by tasks.registering {
    dependsOn(rpmPackage)
    dependsOn(debPackage)
}

/**
 * Docker tasks
 */

val dockerPrepareEnv by tasks.registering(Copy::class) {
    dependsOn(":ontrack-ui:bootJar")
    from("ontrack-ui/build/libs")
    include("*.jar")
    exclude("*-javadoc.jar")
    exclude("*-sources.jar")
    into(project.file("docker"))
    rename(".*", "ontrack.jar")
}

val dockerBuild by tasks.registering(DockerBuildImage::class) {
    dependsOn(dockerPrepareEnv)
    inputDir.set(file("docker"))
    tags.add("nemerosa/ontrack:$version")
    tags.add("nemerosa/ontrack:latest")
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

    val releaseDocCopyJavadoc by tasks.registering(Copy::class) {
        dependsOn("aggregateJavadoc")
        from("build/docs/javadoc")
        into("build/site/release/javadoc/")
    }

    val releaseDocPrepare by tasks.registering {
        dependsOn(releaseDocCopyHtml)
        dependsOn(releaseDocCopyPdf)
        dependsOn(releaseDocCopyJavadoc)
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
        include("ontrack-ui-${version}.jar")
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
    from("build/distributions") {
        include("ontrack*.deb")
        rename { "ontrack.deb" }
    }
    from("build/distributions") {
        include("ontrack*.rpm")
        rename { "ontrack.rpm" }
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
            "build/release/ontrack-postgresql-migration.jar",
            "build/release/ontrack.pdf",
            "build/release/ontrack.deb",
            "build/release/ontrack.rpm"
    )
    body {
        githubReleaseChangeLog.get().changeLog
    }
}

tasks.named("githubRelease") {
    dependsOn(prepareGitHubRelease)
    dependsOn(githubReleaseChangeLog)
}

/**
 * Site generation
 *
 * Must be called AFTER the current version has been promoted in Ontrack to the RELEASE promotion level.
 *
 * This means having a Site job in the pipeline, after the Publish one, calling the `site` task.
 */

val siteOntrackLast2Releases by tasks.registering(OntrackLastReleases::class) {
    releaseCount = 2
    releasePattern = "2\\.[\\d]+\\..*"
}

val siteOntrackLast3Releases by tasks.registering(OntrackLastReleases::class) {
    releaseCount = 6
    releasePattern = "3\\.[\\d]+\\..*"
}

val sitePagesDocJs by tasks.registering {
    dependsOn(siteOntrackLast2Releases)
    dependsOn(siteOntrackLast3Releases)
    outputs.file(project.file("build/site/page/doc.js"))
    doLast {
        val allReleases = siteOntrackLast3Releases.get().releases + siteOntrackLast2Releases.get().releases
        val allVersions = allReleases.joinToString(",") { "'${it.name}'" }
        project.file("build/site/page").mkdirs()
        project.file("build/site/page/doc.js").writeText(
                """var releases = [$allVersions];"""
        )
    }
}

configure<GitPublishExtension> {
    repoUri.set(project.properties["ontrackGitHubUri"] as String)
    branch.set(project.properties["ontrackGitHubPages"] as String)
    contents {
        from("ontrack-site/src/main/web")
        from("build/site/page") {
            include("doc.js")
            into("javascripts/doc/")
        }
    }
    commitMessage.set("GitHub pages for version $version")
}

tasks.named("gitPublishCopy") {
    dependsOn(sitePagesDocJs)
}

val site by tasks.registering {
    dependsOn("gitPublishPush")
}
