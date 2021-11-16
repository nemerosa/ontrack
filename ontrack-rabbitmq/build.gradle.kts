plugins {
    `java-library`
}

dependencies {
    api("org.springframework.amqp:spring-amqp")

    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework:spring-messaging")
}