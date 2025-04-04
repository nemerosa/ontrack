plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-extension-hook"))
    implementation(project(":ontrack-extension-queue"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-general"))
    implementation("io.micrometer:micrometer-core")
    implementation("commons-codec:commons-codec")
    implementation("org.slf4j:slf4j-api")
    implementation("jakarta.annotation:jakarta.annotation-api")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation("com.networknt:json-schema-validator")

    testImplementation(testFixtures(project(":ontrack-extension-casc")))
    testImplementation(testFixtures(project(":ontrack-extension-hook")))
    testImplementation(testFixtures(project(":ontrack-extension-queue")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
