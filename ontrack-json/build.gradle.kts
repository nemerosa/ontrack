plugins {
    `java-library`
}

description = "JSON utilities."

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.commons:commons-lang3")
}
