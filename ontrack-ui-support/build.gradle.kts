plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-model"))
    api(project(":ontrack-extension-api"))
    api("org.springframework:spring-web")
    api("org.springframework:spring-webmvc")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.apache.commons:commons-lang3")

    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
}
