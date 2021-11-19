import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeUp

plugins {
    `java-library`
    id("com.avast.gradle.docker-compose")
}

dependencies {
    testImplementation(project(":ontrack-json"))
    testImplementation("org.springframework.boot:spring-boot-starter")
    testImplementation(project(":ontrack-kdsl-http"))
    testImplementation("commons-io:commons-io")
}

// Pre-acceptance tests: starting the environment

configure<ComposeExtension> {
    createNested("kdslAcceptanceTest").apply {
        useComposeFiles = listOf("src/test/compose/docker-compose.yml")
        projectName = "kdsl"
        environment["ONTRACK_VERSION"] = project.version.toString()
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
        val port = tasks.named<ComposeUp>("kdslAcceptanceTestComposeUp").get().servicesInfos["ontrack"]?.firstContainer?.tcpPort!!
        val url = "http://$host:$port"
        val ontrackUrl: String by rootProject.extra(url)
        logger.info("KDSL Acceptance Test Ontrack URL = $ontrackUrl")
    }
}

// Pre-acceptance tests: stopping the environment

val kdslPostAcceptanceTest by tasks.registering {
    dependsOn("kdslAcceptanceTestComposeDown")
}
