plugins {
    groovy
    `java-library`
}

description = "Git client for Ontrack."

dependencies {
    api("org.eclipse.jgit:org.eclipse.jgit:4.11.5.201810191925-r")

    implementation("org.apache.commons:commons-lang3")
    implementation("org.springframework:spring-tx")
    implementation("commons-io:commons-io")
    implementation("com.google.guava:guava")
    implementation(project(":ontrack-common"))
    implementation("org.codehaus.groovy:groovy")
    implementation("org.slf4j:slf4j-api")
}
