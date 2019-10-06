import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeUp
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.netflix.gradle.plugins.deb.Deb
import com.netflix.gradle.plugins.packaging.SystemPackagingTask
import com.netflix.gradle.plugins.rpm.Rpm
import net.nemerosa.versioning.VersioningExtension
import net.nemerosa.versioning.VersioningPlugin
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.redline_rpm.header.Os

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        val kotlinVersion: String by project
        classpath("com.netflix.nebula:gradle-aggregate-javadocs-plugin:3.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
    }
}

// FIXME Try to use version in gradle.properties
// val springBootVersion: String by project

plugins {
    id("net.nemerosa.versioning") version "2.8.2" apply false
    id("nebula.deb") version "6.2.1"
    id("nebula.rpm") version "6.2.1"
    id("org.sonarqube") version "2.5"
    id("com.avast.gradle.docker-compose") version "0.9.5"
    id("com.bmuschko.docker-remote-api") version "4.1.0"
    id("org.springframework.boot") version "2.1.9.RELEASE" apply false
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
val itJdbcUrl: String by project
val itJdbcUsername: String by project
val itJdbcPassword: String by project
val itJdbcWait: String by project

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

configure(javaProjects) {

    /**
     * For all Java projects
     */

    apply(plugin = "java")
    apply(plugin = "maven-publish")

    // Javadoc

    if (hasProperty("documentation")) {

        tasks.register<Jar>("javadocJar") {
            archiveClassifier.set("javadoc")
            from("javadoc")
        }

        // Sources

        tasks.register<Jar>("sourcesJar") {
            dependsOn(JavaPlugin.CLASSES_TASK_NAME)
            archiveClassifier.set("sources")
            from(project.the<SourceSetContainer>()["main"].allSource)
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
                pom {
                    name.set(this@p.name)
                    description.set(this@p.description)
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
                            email.set("damien.coraboeuf@gmail.com")
                        }
                    }
                }
            }
        }
    }

//    model {
//        ValidationRunWithAutoStampIT
//        tasks.generatePomFileForMavenCustomPublication {
//            destination = file("${buildDir}/poms/${project.name}-${version}.pom")
//        }
//    }

    afterEvaluate {
        tasks.assemble.dependsOn "generatePomFileForMavenCustomPublication"
    }

    // Archives for Javadoc and Sources

    artifacts {
        if (documentationProfile) {
            archives javadocJar
            archives sourceJar
        }
    }

}

configure(coreProjects) {

    /**
     * For all Java projects
     */

    apply plugin: "kotlin"
    apply plugin: "kotlin-spring"
    apply plugin: "io.spring.dependency-management"

    dependencyManagement {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES) {
                bomProperty("kotlin.version", kotlinVersion)
            }
        }
        dependencies {
            dependency "commons-io:commons-io:2.6"
            dependency "org.apache.commons:commons-text:1.6"
            dependency "net.jodah:failsafe:1.1.1"
            dependency "commons-logging:commons-logging:1.2"
            dependency "org.apache.commons:commons-math3:3.6.1"
            dependency "com.google.guava:guava:27.0.1-jre"
            dependency "args4j:args4j:2.33"
            dependency "org.jgrapht:jgrapht-core:1.3.0"
            dependency "org.kohsuke:groovy-sandbox:1.19"
            dependency "com.graphql-java:graphql-java:11.0"
            dependency "org.jetbrains.kotlin:kotlin-test:${kotlinVersion}"
            // Overrides from Spring Boot
            dependency "org.postgresql:postgresql:9.4.1208"
        }
    }

    dependencies {
        compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}"
        compile "org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}"
        compileOnly "org.projectlombok:lombok:1.18.10"
        annotationProcessor "org.projectlombok:lombok:1.18.10"
        testCompileOnly "org.projectlombok:lombok:1.18.10"
        testAnnotationProcessor "org.projectlombok:lombok:1.18.10"
        // Testing
        testCompile "junit:junit"
        testCompile "org.mockito:mockito-core"
        testCompile "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
        testCompile "org.jetbrains.kotlin:kotlin-test"
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    // Unit tests run with the `test` task
    test {
        include "**/*Test.class"
        reports {
            html.enabled = false
        }
    }

    // Integration tests
    task integrationTest(type: Test) {
        mustRunAfter "test"
        include "**/*IT.class"
        minHeapSize = "512m"
        maxHeapSize = "1024m"
        dependsOn preIntegrationTest
        finalizedBy postIntegrationTest
        /**
         * Sets the JDBC URL
         */
        doFirst {
            println "Setting JDBC URL for IT: ${rootProject.ext.jdbcUrl}"
            systemProperty "spring.datasource.url", rootProject.ext.jdbcUrl
            systemProperty "spring.datasource.username", "ontrack"
            systemProperty "spring.datasource.password", "ontrack"
        }
    }

    // Synchronization with shutting down the database
    rootProject.integrationTestComposeDown.mustRunAfter project.integrationTest

}

/**
 * Packaging of Ontrack for the subsequent stages of the validation.
 *
 * This creates a ZIP which contains:
 *
 * - all Gradle files needed for the execution of the pipeline
 * - the `buildSrc` which contains the Gradle helper classes
 * - the UI JAR artifact
 * - the Acceptance JAR artifact
 * - a ZIP containing ALL artifacts (POM files, JAR, Javadoc & Sources)
 * - an `ontrack.properties` file which contains the list of modules & the version information
 */

apply plugin: "base"

// Global Javadoc

if (documentationProfile) {
    apply plugin: "nebula-aggregate-javadocs"

    gradle.projectsEvaluated {
        aggregateJavadocs {
            includes = ["net/nemerosa/**"]
        }

        rootProject.tasks.create("javadocPackage", Zip) {
            classifier = "javadoc"
            archiveName = "ontrack-javadoc.zip"
            dependsOn aggregateJavadocs
            from rootProject.file("$rootProject.buildDir/docs/javadoc")
        }
    }
}

// ZIP package which contains all artifacts to be published

task publicationPackage(type: Zip) {
    classifier = "publication"
    archiveName = "ontrack-publication.zip"
    subprojects {
        afterEvaluate {
            if (tasks.findByName("jar")) {
                dependsOn assemble
                if (jar.isEnabled()) {
                    from jar
                }
                if (documentationProfile) {
                    if (tasks.findByName("javadocJar")) from javadocJar
                    if (tasks.findByName("sourceJar")) from sourceJar
                }
                if (tasks.findByName("testJar")) from testJar
                // POM file
                from "${project.buildDir}/poms/${project.name}-${project.version}.pom"
            }
        }
    }
    // Extension test
    from("${rootProject.file("ontrack-extension-test")}") {
        into "ontrack-extension-test"
    }
}

if (documentationProfile) {
    gradle.projectsEvaluated {
        publicationPackage {
            from javadocPackage
        }
    }
}

// Ontrack descriptor

task deliveryDescriptor {
    ext.output = project.file("build/ontrack.properties")
    doLast {
        (output as File).parentFile.mkdirs()
        output.text = "# Ontrack properties\n"
        // Version
        output << "# Version information"
        output << "VERSION_BUILD = ${project.versioning.info.build}\n"
        output << "VERSION_BRANCH = ${project.versioning.info.branch}\n"
        output << "VERSION_BASE = ${project.versioning.info.base}\n"
        output << "VERSION_BRANCHID = ${project.versioning.info.branchId}\n"
        output << "VERSION_BRANCHTYPE = ${project.versioning.info.branchType}\n"
        output << "VERSION_COMMIT = ${project.versioning.info.commit}\n"
        output << "VERSION_DISPLAY = ${project.versioning.info.display}\n"
        output << "VERSION_FULL = ${project.versioning.info.full}\n"
        output << "VERSION_SCM = ${project.versioning.info.scm}\n"
        // Modules
        output << "# Comma-separated list of modules\n"
        output << "MODULES = ${project.subprojects.findAll { it.tasks.findByName("jar") }.collect { it.name }.join(",")}\n"
    }
}

// Delivery package

task deliveryPackage(type: Zip) {
    classifier = "delivery"
    // Gradle files
    from(projectDir) {
        include "buildSrc/**"
        include "*.gradle"
        include "gradlew*"
        include "gradle/**"
        include "gradle.properties"
        exclude "**/.gradle/**"
        exclude "**/build/**"
    }
    // Acceptance
    dependsOn ":ontrack-acceptance:normaliseJar"
    from(project(":ontrack-acceptance").file("src/main/compose")) {
        into "ontrack-acceptance"
    }
    // All artifacts
    dependsOn publicationPackage
    from publicationPackage
    // Descriptor
    dependsOn deliveryDescriptor
    from deliveryDescriptor.output
}

build.dependsOn deliveryPackage

/**
 * Packaging for OS
 *
 * The package version does not accept versions like the ones generated
 * from the Versioning plugin for the feature branches for example.
 */

def packageVersion
if (version ==~ /\d+\.\d+\.\d+/) {
    packageVersion = version.replaceAll(/[^0-9\.-_]/, "")
} else {
    packageVersion = "0.0.0"
}
println "Using package version = ${packageVersion}"

task debPackage(type: Deb) {
    dependsOn(":ontrack-ui:bootJar")

    link("/etc/init.d/ontrack", "/opt/ontrack/bin/ontrack.sh")
}

task rpmPackage(type: Rpm) {
    dependsOn(":ontrack-ui:bootJar")

    user = "ontrack"
    link("/etc/init.d/ontrack", "/opt/ontrack/bin/ontrack.sh")
}

tasks.withType(SystemPackagingTask) {

    packageName = "ontrack"
    release = "1"
    version = packageVersion
    os = Os.LINUX // only applied to RPM

    preInstall("gradle/os-package/preInstall.sh")
    postInstall("gradle/os-package/postInstall.sh")

    from(project(":ontrack-ui").file("build/libs")) {
        include("ontrack-ui-${project.version}.jar")
        into("/opt/ontrack/lib")
        rename(".*", "ontrack.jar")
    }

    from("gradle/os-package") {
        include("ontrack.sh")
        into("/opt/ontrack/bin")
        fileMode = 0x168 // 0550
    }
}

task osPackages {
    dependsOn(rpmPackage)
    dependsOn(debPackage)
}

/**
 * Docker tasks
 */

task dockerPrepareEnv(type: Copy, dependsOn: ":ontrack-ui:bootJar") {
    from "ontrack-ui/build/libs"
    include "*.jar"
    exclude "*-javadoc.jar"
    exclude "*-sources.jar"
    into project.file("docker")
    rename ".*", "ontrack.jar"
}

task dockerBuild(type: DockerBuildImage) {
    dependsOn dockerPrepareEnv
    inputDir = file("docker")
    tags.add("nemerosa/ontrack:$version")
    tags.add("nemerosa/ontrack:latest")
}

/**
 * Acceptance tasks
 */

import net.nemerosa.ontrack.gradle.*

dockerCompose {
    ci {
        useComposeFiles = ["${rootDir}/gradle/compose/docker-compose.yml", "${rootDir}/gradle/compose/docker-compose-ci.yml"]
        projectName = "ci"
        forceRecreate = true
        tcpPortsToIgnoreWhenWaiting = [8083, 8086]
    }
}

task ciStart {
    dependsOn ciComposeUp
    // When done
    doLast {
        def host = ciComposeUp.servicesInfos.ontrack.firstContainer.host as String
        def port = ciComposeUp.servicesInfos.ontrack.firstContainer.ports.get(8080) as int
        ext.ontrackUrl = "https://$host:$port"
        logger.info("Ontrack URL = ${ext.ontrackUrl}")
    }
}

task ciStop {
    dependsOn ciComposeDown
}

task ciAcceptanceTest(type: RemoteAcceptanceTest) {
    acceptanceUrl = { ciStart.ontrackUrl }
    disableSsl = true
    acceptanceTimeout = 300
    acceptanceImplicitWait = 30
    dependsOn ciStart
    finalizedBy ciStop
}

ciComposeDown.mustRunAfter ciAcceptanceTest

/**
 * Development tasks
 */

dockerCompose {
    dev {
        useComposeFiles = ["${rootDir}/gradle/compose/docker-compose-dev.yml"]
        projectName = "dev"
        forceRecreate = false
        environment.put("POSTGRES_NAME", project.properties.devPostgresName)
        environment.put("POSTGRES_PORT", project.properties.devPostgresPort)
    }
}

task devStart {
    dependsOn devComposeUp
}

task devStop {
    dependsOn devComposeDown
}

/**
 * Publication tasks
 *
 * Standalone Gradle tasks in `gradle/publication.gradle` and in
 * `gradle/production.gradle`
 */

