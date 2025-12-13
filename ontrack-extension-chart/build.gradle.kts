plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-ui-graphql"))
    implementation("org.apache.commons:commons-math3")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
