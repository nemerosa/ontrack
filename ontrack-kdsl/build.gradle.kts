plugins {
    `java-library`
    id("com.apollographql.apollo").version("2.5.14")
}

dependencies {
    api(project(":ontrack-json"))

    api("com.apollographql.apollo:apollo-runtime:2.5.14")
    api("com.apollographql.apollo:apollo-coroutines-support:2.5.14")
    api("org.springframework.boot:spring-boot-starter-web")

    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.3")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.3.4")
}

apollo {
    @Suppress("UnstableApiUsage")
    customTypeMapping.set(mapOf(
            "LocalDateTime" to "java.time.LocalDateTime",
            "UUID" to "java.util.UUID",
            "JSON" to "com.fasterxml.jackson.databind.JsonNode",
            "Long" to "java.lang.Long"
    ))
}

tasks.named("javadoc", Javadoc::class) {
    exclude("net/nemerosa/ontrack/kdsl/connector/graphql/schema/**")
}