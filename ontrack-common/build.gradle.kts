plugins {
    `java-library`
    `java-test-fixtures`
}

description = "Common types for Ontrack."

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-io:commons-io")
    implementation("org.slf4j:slf4j-api")
    implementation("org.jsoup:jsoup")
}
