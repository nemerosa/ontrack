plugins {
    `java-library`
    id("com.apollographql.apollo").version("2.5.13")
}

dependencies {
    api(project(":ontrack-json"))

    api("com.apollographql.apollo:apollo-runtime:2.5.13")
    api("com.apollographql.apollo:apollo-coroutines-support:2.5.13")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.springframework.boot:spring-boot-starter-web")
}

apollo {
    @Suppress("UnstableApiUsage")
    customTypeMapping.set(mapOf(
        "LocalDateTime" to "java.time.LocalDateTime",
        "UUID" to "java.util.UUID",
        "JSON" to "com.fasterxml.jackson.databind.JsonNode"
    ))
}

tasks.named("javadoc", Javadoc::class) {
    exclude("net/nemerosa/ontrack/kdsl/connector/graphql/schema/**")
}