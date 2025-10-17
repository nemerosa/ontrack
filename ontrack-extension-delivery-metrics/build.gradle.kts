plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-extension-chart"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-git"))
    implementation(project(":ontrack-ui-graphql"))
    implementation("org.apache.commons:commons-math3")
    implementation(project(":ontrack-repository-support"))

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(testFixtures(project(":ontrack-extension-api")))
    testImplementation(testFixtures(project(":ontrack-extension-casc")))
    testImplementation("com.networknt:json-schema-validator")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
    testRuntimeOnly(project(":ontrack-extension-auto-versioning"))
}
