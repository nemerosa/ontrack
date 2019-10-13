plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-jdbc")
    implementation("org.flywaydb:flyway-core")
    implementation(project(":ontrack-model"))
    implementation("org.slf4j:slf4j-api")
}
