import com.avast.gradle.dockercompose.ComposeExtension

plugins {
    `java-library`
    id("com.avast.gradle.docker-compose")
}

dependencies {
    testImplementation(project(":ontrack-json"))
    testImplementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework:spring-web")
    testImplementation(project(":ontrack-kdsl"))
    testImplementation("commons-io:commons-io")
    testImplementation("commons-codec:commons-codec")

    testImplementation("org.influxdb:influxdb-java")
}

// Pre-acceptance tests: starting the environment

configure<ComposeExtension> {
    createNested("kdslAcceptanceTest").apply {
        useComposeFiles.addAll(listOf("src/test/compose/docker-compose.yml"))
        setProjectName("kdsl")
        // environment.put("ONTRACK_VERSION", project.version.toString())
        captureContainersOutputToFiles.set(file("build/logs/containers"))
        composeLogToFile.set(file("build/logs/compose"))
        retainContainersOnStartupFailure.set(true)
    }
}

val isCI = System.getenv("CI") == "true"

val kdslAcceptanceTestComposeUp by tasks.named("kdslAcceptanceTestComposeUp") {
    if (!isCI) {
        dependsOn(":dockerBuild")
        dependsOn(":ontrack-web-core:dockerBuild")
    }
}

// Pre-acceptance tests: stopping the environment

val kdslAcceptanceTestComposeDown by tasks.named("kdslAcceptanceTestComposeDown")

// Restricting unit tests

tasks.named<Test>("test") {
    useJUnitPlatform()
    exclude("**/ACC*")
}

// Running the acceptance tests

val kdslAcceptanceTest by tasks.registering(Test::class) {
    useJUnitPlatform()
    mustRunAfter("test")
    include("**/ACC*.class")
    minHeapSize = "512m"
    maxHeapSize = "3072m"
    dependsOn(kdslAcceptanceTestComposeUp)
    if (!isCI) {
        finalizedBy(kdslAcceptanceTestComposeDown)
    }
    /**
     * Sets the Ontrack URL
     */
    doFirst {
        systemProperty("ontrack.acceptance.connection.url", "http://localhost:8080")
        systemProperty("ontrack.acceptance.connection.username", "admin")
        systemProperty("ontrack.acceptance.connection.password", "admin")
        systemProperty("ontrack.acceptance.connection.mgt.url", "http://localhost:8800")
    }
}