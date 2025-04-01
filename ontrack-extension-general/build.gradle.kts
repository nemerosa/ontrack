plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-ui-graphql"))
    implementation("org.apache.commons:commons-lang3")
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-indicators"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-casc")))
    testImplementation("com.networknt:json-schema-validator")
    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
