plugins {
    `java-library`
}

dependencies {
    implementation(project(":ontrack-common"))
    implementation(project(":ontrack-model"))
    implementation(project(":ontrack-test-utils"))
    implementation(project(":ontrack-extension-support"))
    implementation(project(":ontrack-ui-support"))
    implementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-jdbc")
    implementation("org.springframework:spring-test")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.slf4j:slf4j-api")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("io.micrometer:micrometer-core")
    implementation("com.google.guava:guava")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-core")
}