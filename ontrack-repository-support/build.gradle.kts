plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-model"))
    api("org.springframework:spring-jdbc")

    implementation("org.springframework:spring-context")
    implementation("org.apache.commons:commons-lang3")
}