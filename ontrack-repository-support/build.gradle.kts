plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-model"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-jdbc")
    implementation("org.apache.commons:commons-lang3")
}