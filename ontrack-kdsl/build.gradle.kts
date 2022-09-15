plugins {
    `java-library`
    id("com.apollographql.apollo").version("2.5.11")
}

dependencies {
    api(project(":ontrack-json"))

    api("com.apollographql.apollo:apollo-runtime:2.5.11")
    api("com.apollographql.apollo:apollo-coroutines-support:2.5.11")
    api("org.springframework.boot:spring-boot-starter-web")

    implementation("org.apache.httpcomponents:httpclient:4.5.13")
}

apollo {
    @Suppress("UnstableApiUsage")
    customTypeMapping.set(mapOf(
        "LocalDateTime" to "java.time.LocalDateTime",
        "UUID" to "java.util.UUID"
    ))
}

tasks.named("javadoc", Javadoc::class) {
    exclude("net/nemerosa/ontrack/kdsl/connector/graphql/schema/**")
}