plugins {
    `java-library`
}

description = "Abstract management of identified jobs."

dependencies {
    implementation("org.apache.commons:commons-lang3")
    implementation(project(":ontrack-common"))
    implementation("org.springframework:spring-context")
    implementation("org.slf4j:slf4j-api")
    implementation("io.micrometer:micrometer-core")

    testImplementation(project(":ontrack-test-utils"))
    testImplementation("org.apache.commons:commons-math3")
    testImplementation("org.slf4j:slf4j-log4j12")
}
