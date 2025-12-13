plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api(project(":ontrack-model"))
    api("org.springframework.security:spring-security-config")
    api("org.springframework.security:spring-security-web")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.apache.commons:commons-lang3")

    testFixturesImplementation("org.jetbrains.kotlin:kotlin-test")
}
