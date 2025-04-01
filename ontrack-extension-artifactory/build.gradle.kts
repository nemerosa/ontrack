plugins {
    `java-library`
}

dependencies {
    implementation(project(":ontrack-extension-support"))
    implementation("org.springframework:spring-tx")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.slf4j:slf4j-api")

    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))

}
