apply(plugin = "org.springframework.boot")

dependencies {

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.apache.commons:commons-lang3")

    runtimeOnly(project(":ontrack-database"))
    runtimeOnly("com.h2database:h2:1.4.197")

}
