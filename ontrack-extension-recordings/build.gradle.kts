plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-repository-support"))
    implementation(project(":ontrack-extension-casc"))
    implementation("io.micrometer:micrometer-core")
    implementation("org.slf4j:slf4j-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.apache.commons:commons-lang3")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation("com.networknt:json-schema-validator")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
