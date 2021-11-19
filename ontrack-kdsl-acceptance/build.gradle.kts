plugins {
    `java-library`
}

dependencies {
    testImplementation(project(":ontrack-json"))
    testImplementation("org.springframework.boot:spring-boot-starter")
    testImplementation(project(":ontrack-kdsl-http"))
    testImplementation(project(path = ":ontrack-extension-github", configuration = "kdsl"))
    testImplementation("commons-io:commons-io")
}
