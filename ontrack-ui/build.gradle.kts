plugins {
    `java-library`
    id("com.google.cloud.tools.jib")
}

apply(plugin = "org.springframework.boot")

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.springframework.boot:spring-boot-starter-thymeleaf")
    api(project(":ontrack-ui-support"))
    api(project(":ontrack-ui-graphql"))
    api(project(":ontrack-extension-api"))
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-job"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-text")
    implementation("commons-io:commons-io")
    implementation("jakarta.validation:jakarta.validation-api")

    runtimeOnly(project(":ontrack-service"))
    runtimeOnly(project(":ontrack-repository-impl"))
    runtimeOnly(project(":ontrack-rabbitmq"))
    runtimeOnly(project(":ontrack-database"))

    // Metric runtimes
    // TODO runtimeOnly("io.micrometer:micrometer-registry-influx")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    // TODO runtimeOnly("io.micrometer:micrometer-registry-elastic")

    // TODO Logging extensions
    runtimeOnly("net.logstash.logback:logstash-logback-encoder")

    testImplementation(testFixtures(project(":ontrack-ui-support")))
//    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
//    testImplementation(project(":ontrack-extension-general"))
//    testImplementation(project(":ontrack-extension-casc"))
//    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-api")))
//    testImplementation("com.networknt:json-schema-validator")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // List of extensions needed for the documentation generation
//    testImplementation(project(":ontrack-extension-notifications"))
//    testImplementation(project(":ontrack-extension-workflows"))
    testImplementation("org.junit.platform:junit-platform-suite-api")
    testImplementation("org.junit.platform:junit-platform-suite-engine")

    // List of extensions to include in core
    runtimeOnly(project(":ontrack-extension-general"))
    // TODO runtimeOnly(project(":ontrack-extension-ldap"))
    // TODO runtimeOnly(project(":ontrack-extension-oidc"))
    // TODO runtimeOnly(project(":ontrack-extension-jenkins"))
    // TODO runtimeOnly(project(":ontrack-extension-jira"))
    runtimeOnly(project(":ontrack-extension-artifactory"))
    runtimeOnly(project(":ontrack-extension-issues"))
    runtimeOnly(project(":ontrack-extension-scm"))
    runtimeOnly(project(":ontrack-extension-git"))
    // TODO runtimeOnly(project(":ontrack-extension-github"))
    // TODO runtimeOnly(project(":ontrack-extension-gitlab"))
    // TODO runtimeOnly(project(":ontrack-extension-stash"))
    // TODO runtimeOnly(project(":ontrack-extension-bitbucket-cloud"))
    // TODO runtimeOnly(project(":ontrack-extension-combined"))
    runtimeOnly(project(":ontrack-extension-stale"))
    // TODO runtimeOnly(project(":ontrack-extension-vault"))
    // TODO runtimeOnly(project(":ontrack-extension-influxdb"))
    // TODO runtimeOnly(project(":ontrack-extension-sonarqube"))
    runtimeOnly(project(":ontrack-extension-indicators"))
    runtimeOnly(project(":ontrack-extension-casc"))
    // TODO runtimeOnly(project(":ontrack-extension-elastic"))
    // TODO runtimeOnly(project(":ontrack-extension-slack"))
    // TODO runtimeOnly(project(":ontrack-extension-chart"))
    // TODO runtimeOnly(project(":ontrack-extension-delivery-metrics"))
    // TODO runtimeOnly(project(":ontrack-extension-auto-versioning"))
    // TODO runtimeOnly(project(":ontrack-extension-license"))
    // TODO runtimeOnly(project(":ontrack-extension-tfc"))
    runtimeOnly(project(":ontrack-extension-recordings"))
    runtimeOnly(project(":ontrack-extension-notifications"))
    // TODO runtimeOnly(project(":ontrack-extension-hook"))
    runtimeOnly(project(":ontrack-extension-queue"))
    // TODO runtimeOnly(project(":ontrack-extension-workflows"))
    // TODO runtimeOnly(project(":ontrack-extension-environments"))
}


jib {
    to {
        image = "nemerosa/ontrack"
        tags = setOf(version as String, "latest")
    }
    from {
        image = "azul/zulu-openjdk-alpine:17"
    }
    container {
        ports = listOf("8080", "8800")
    }
}

tasks.named("jibDockerBuild") {
    shouldRunAfter("integrationTest")
}
