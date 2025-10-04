plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-extension-support"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-extension-license"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-repository"))
    implementation(project(":ontrack-repository-support"))

    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
