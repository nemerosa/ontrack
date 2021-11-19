import com.avast.gradle.dockercompose.ComposeExtension

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