plugins {
    `java-library`
}

dependencies {
    api("junit:junit")
    api(project(":ontrack-json"))

    implementation("org.apache.commons:commons-lang3")
    implementation("commons-io:commons-io")
    implementation("org.jetbrains.kotlin:kotlin-test")
}
