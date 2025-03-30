plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-jdbc")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation(project(":ontrack-model"))
    implementation("org.slf4j:slf4j-api")
}
