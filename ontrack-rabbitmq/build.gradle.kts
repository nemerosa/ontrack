plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework:spring-messaging")
}