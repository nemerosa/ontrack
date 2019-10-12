import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    groovy
}

description = "DSL Shell for Ontrack."

apply(plugin = "org.springframework.boot")

dependencies {
    compile("args4j:args4j")
    compile("org.slf4j:slf4j-api")
    compile("org.apache.httpcomponents:httpclient")
    compile("org.apache.httpcomponents:httpmime")
    compile("org.springframework.boot:spring-boot-starter")
    compile(project(":ontrack-dsl"))
}

tasks.getByName<Jar>("jar") {
    enabled = true
}


tasks.getByName<BootJar>("bootJar") {
    mainClassName = "net.nemerosa.ontrack.shell.ShellApplication"
    launchScript()
    archiveClassifier.set("executable")
}

rootProject.tasks.named<Zip>("publicationPackage") {
    from(project(":ontrack-dsl-shell").tasks["bootJar"])
}
