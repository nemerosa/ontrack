plugins {
    `java-library`
}

dependencies {
    api("org.springframework:spring-test")
    api(project(":ontrack-common"))
    api(project(":ontrack-model"))
    api(project(":ontrack-test-utils"))
    api("org.springframework:spring-jdbc")
    api("org.springframework.security:spring-security-core")
    api("org.springframework.security:spring-security-oauth2-jose")
    api("org.jetbrains.kotlin:kotlin-test")

    implementation(project(":ontrack-extension-support"))
    implementation(project(":ontrack-ui-support"))
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.slf4j:slf4j-api")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("io.micrometer:micrometer-core")

    implementation("io.mockk:mockk")
    implementation("io.mockk:mockk-jvm")
    implementation("io.mockk:mockk-dsl")
    implementation("io.mockk:mockk-dsl-jvm")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-core")
}