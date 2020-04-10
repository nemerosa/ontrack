import org.springframework.boot.gradle.tasks.bundling.BootJar

apply(plugin = "org.springframework.boot")

plugins {
    `java-library`
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.apache.commons:commons-lang3")

    runtimeOnly(project(":ontrack-database"))
    runtimeOnly("com.h2database:h2:1.4.197")

}

val bootJar by tasks.named<BootJar>("bootJar") {
    archiveClassifier.set("app")
}

tasks.named("assemble") {
    dependsOn(bootJar)
}

publishing {
    publications {
        named<MavenPublication>("mavenCustom") {
            setArtifacts(listOf(bootJar))
        }
    }
}
