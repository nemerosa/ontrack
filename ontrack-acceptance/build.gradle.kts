import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    groovy
}

apply(plugin = "org.springframework.boot")
apply(plugin = "com.bmuschko.docker-remote-api")

val seleniumVersion = "3.11.0"

dependencies {
    testCompile(project(":ontrack-client"))
    testCompile(project(":ontrack-dsl"))
    testCompile(project(":ontrack-dsl-shell"))
    testCompile(project(":ontrack-test-utils"))
    testCompile("org.codehaus.groovy:groovy")
    testCompile("org.codehaus.groovy:groovy-xml")
    testCompile("org.springframework.boot:spring-boot-starter")

    testCompile("org.influxdb:influxdb-java")

    testCompile("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testCompile("org.seleniumhq.selenium:selenium-support:$seleniumVersion")
}

//noinspection GroovyAssignabilityCheck
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.seleniumhq.selenium" && requested.name != "htmlunit-driver") {
            useVersion(seleniumVersion)
        }
    }
}

/**
 * Packaging
 */

val bootJar = tasks.getByName<BootJar>("bootJar") {
    bootInf {
        from(sourceSets["test"].output)
        into("classes")
    }
    classpath(configurations.named("testRuntime"))
    mainClassName = "net.nemerosa.ontrack.acceptance.boot.Start"
    archiveClassifier.set("app")
}

val normaliseJar by tasks.registering(Copy::class) {
    dependsOn(bootJar)
    from("$buildDir/libs/")
    include("ontrack-acceptance-$version.jar")
    into("$buildDir/libs/")
    rename("ontrack-acceptance-$version.jar", "ontrack-acceptance.jar")
}

val acceptanceDockerPrepareEnv by tasks.registering(Copy::class) {
    dependsOn(normaliseJar)
    from("${buildDir}/libs/ontrack-acceptance.jar")
    into("${projectDir}/src/main/docker")
}

tasks.named("assemble") {
    dependsOn(normaliseJar)
}

val dockerBuild by tasks.registering(DockerBuildImage::class) {
    dependsOn(acceptanceDockerPrepareEnv)
    inputDir.set(file("src/main/docker"))
    tags.add("nemerosa/ontrack-acceptance:$version")
    tags.add("nemerosa/ontrack-acceptance:latest")
}

rootProject.tasks.named<Zip>("publicationPackage") {
    from(bootJar)
}

/**
 * Local test definitions
 */

val ontrackUrl: String by project
val ontrackJvmOptions: String by project
val ontrackImplicitWait: String by project

val acceptanceTest by tasks.registering(Test::class) {
    outputs.upToDateWhen { false }  // Always run tests
    include("**/ACC*.class")
    ignoreFailures = true
    systemProperties(
            mapOf(
                    "ontrack.url" to ontrackUrl,
                    "ontrack.implicitWait" to ontrackImplicitWait
            )
    )
}

// Disable unit tests (none in this project)

tasks.named<Test>("test") {
    enabled = false
}
