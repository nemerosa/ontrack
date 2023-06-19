import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeUp

plugins {
    groovy
    id("com.avast.gradle.docker-compose")
}

val seleniumVersion = "4.9.1"

dependencies {
    implementation("org.codehaus.groovy:groovy")

    testImplementation(project(":ontrack-client"))
    testImplementation(project(":ontrack-dsl-v4"))
    testImplementation(project(":ontrack-dsl-shell"))
    testImplementation(project(":ontrack-test-utils"))
    testImplementation("org.apache.commons:commons-lang3")
    testImplementation("commons-io:commons-io")
    testImplementation("org.codehaus.groovy:groovy-xml")
    testImplementation("org.springframework.boot:spring-boot-starter")

    testImplementation("org.influxdb:influxdb-java")
    testImplementation("org.keycloak:keycloak-admin-client:21.1.1")

    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testImplementation("org.seleniumhq.selenium:selenium-support:$seleniumVersion")
}

// Pre-acceptance tests: starting the environment

configure<ComposeExtension> {
    createNested("acceptance").apply {
        useComposeFiles.addAll(listOf("src/main/compose/docker-compose.yml"))
        setProjectName("acceptance")
        environment.put("ONTRACK_VERSION", project.version.toString())
        captureContainersOutputToFiles.set(file("build/logs/containers"))
    }
}

val acceptanceComposeUp by tasks.named<ComposeUp>("acceptanceComposeUp") {
    dependsOn(":dockerBuild")
}

val preAcceptanceTest by tasks.registering {
    dependsOn(acceptanceComposeUp)
}

// Running the acceptance tests

val acceptanceTest by tasks.registering(Test::class) {
    useJUnit()
    mustRunAfter("test")
    include("**/ACC*.class")
    minHeapSize = "512m"
    maxHeapSize = "3072m"
    dependsOn(preAcceptanceTest)
    finalizedBy(postAcceptanceTest)
    /**
     * Sets the Ontrack URL
     */
    doFirst {
        val host = "localhost"

        val ontrackPort = acceptanceComposeUp.servicesInfos["ontrack"]!!.firstContainer!!.ports[8080]
        val ontrackUrl ="http://$host:$ontrackPort"

        val seleniumGridPort = acceptanceComposeUp.servicesInfos["selenium"]?.firstContainer!!.ports[4444]
        val seleniumUrl = "http://$host:$seleniumGridPort"

        val influxDbPort = acceptanceComposeUp.servicesInfos["influxdb"]?.firstContainer!!.ports[8086]
        val influxDbUrl = "http://$host:$influxDbPort"

        println("Ontrack URL  = $ontrackUrl")
        println("Selenium URL = $seleniumUrl")
        println("InfluxDB URL = $influxDbUrl")

        environment["ONTRACK_ACCEPTANCE_URL"] = ontrackUrl

        environment["ONTRACK_ACCEPTANCE_SELENIUM_GRID_URL"] = seleniumUrl
        environment["ONTRACK_ACCEPTANCE_SELENIUM_TARGET_URL"] = "http://ontrack:8080" // Local URL
        environment["ONTRACK_ACCEPTANCE_SELENIUM_BROWSER_NAME"] = "chrome"

        environment["ONTRACK_ACCEPTANCE_INFLUXDB_URI"] = influxDbUrl

        environment["ONTRACK_ACCEPTANCE_IMPLICIT_WAIT"] = "60"
    }
}

// Post-acceptance tests: stopping the environment

val acceptanceComposeDown by tasks.named("acceptanceComposeDown") {
    mustRunAfter("acceptanceTest")
}

val postAcceptanceTest by tasks.registering {
    dependsOn(acceptanceComposeDown)
}

// Disable unit tests (none in this project)

tasks.named<Test>("test") {
    enabled = false
}

// No Javadoc for this module

tasks.named<Javadoc>("javadoc") {
    enabled = false
}
