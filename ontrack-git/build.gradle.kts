plugins {
    `java-library`
}

description = "Git client for Ontrack."

dependencies {
    api("org.eclipse.jgit:org.eclipse.jgit:6.8.0.202311291450-r")

    implementation("org.apache.commons:commons-lang3")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("commons-io:commons-io")
    implementation(project(":ontrack-common"))
    implementation(project(":ontrack-extension-api"))
    implementation("org.slf4j:slf4j-api")
    implementation("io.micrometer:micrometer-core")
}
