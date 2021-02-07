import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    groovy
    `java-library`
}

description = "DSL Shell for Ontrack."

apply(plugin = "org.springframework.boot")

dependencies {
    implementation("args4j:args4j")
    implementation("org.slf4j:slf4j-api")
    implementation("org.apache.httpcomponents:httpclient")
    implementation("org.apache.httpcomponents:httpmime")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(project(":ontrack-dsl-v4"))
}

tasks.getByName<Jar>("jar") {
    enabled = true
}


tasks.getByName<BootJar>("bootJar") {
    mainClassName = "net.nemerosa.ontrack.shell.ShellApplication"
    launchScript()
    archiveClassifier.set("executable")
}
    