import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeUp

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
        environment.put("ONTRACK_VERSION", project.version.toString())
        captureContainersOutputToFiles.set(file("build/logs/containers"))
    }
}

val kdslAcceptanceTestComposeUp by tasks.named("kdslAcceptanceTestComposeUp") {
    dependsOn(":dockerBuild")
}

val kdslPreAcceptanceTest by tasks.registering {
    dependsOn("kdslAcceptanceTestComposeUp")
    // When done
    doLast {
        val host = tasks.named<ComposeUp>("kdslAcceptanceTestComposeUp").get().servicesInfos["ontrack"]?.host!!
        val ontrackContainer = tasks.named<ComposeUp>("kdslAcceptanceTestComposeUp").get().servicesInfos["ontrack"]?.firstContainer!!
        val uiPort = ontrackContainer.ports[8080]
        val mgtPort = ontrackContainer.ports[8800]
        val ontrackUrl: String by rootProject.extra("http://$host:$uiPort")
        val ontrackMgtUrl: String by rootProject.extra("http://$host:$mgtPort/manage")
        logger.info("KDSL Acceptance Test Ontrack URL = $ontrackUrl")
        logger.info("KDSL Acceptance Test Ontrack Mgt URL = $ontrackMgtUrl")
    }
}

// Pre-acceptance tests: stopping the environment

val kdslPostAcceptanceTest by tasks.registering {
    dependsOn("kdslAcceptanceTestComposeDown")
}

val kdslAcceptanceTestComposeDown by tasks.named("kdslAcceptanceTestComposeDown") {
    mustRunAfter("kdslAcceptanceTest")
}

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
    dependsOn(kdslPreAcceptanceTest)
    finalizedBy(kdslPostAcceptanceTest)
    /**
     * Sets the Ontrack URL
     */
    doFirst {
        val ontrackUrl = rootProject.ext["ontrackUrl"] ?: error("ontrackUrl must be defined")
        val ontrackMgtUrl = rootProject.ext["ontrackMgtUrl"] ?: error("ontrackMgtUrl must be defined")
        println("Setting Ontrack URL for KDSL Acceptance Tests: $ontrackUrl")
        println("Setting Ontrack Mgt URL for KDSL Acceptance Tests: $ontrackMgtUrl")
        systemProperty("ontrack.acceptance.connection.url", ontrackUrl)
        systemProperty("ontrack.acceptance.connection.username", "admin")
        systemProperty("ontrack.acceptance.connection.password", "admin")
        systemProperty("ontrack.acceptance.connection.mgt.url", ontrackMgtUrl)
    }
}