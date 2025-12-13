plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))

    implementation("org.slf4j:slf4j-api")
    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-extension-casc"))

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}