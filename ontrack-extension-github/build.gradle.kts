import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-git"))

    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-queue"))
    implementation("org.apache.commons:commons-lang3")
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-indicators"))
    implementation("io.jsonwebtoken:jjwt-api")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.micrometer:micrometer-core")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    runtimeOnly("io.jsonwebtoken:jjwt-impl")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson")
    runtimeOnly(project(":ontrack-rabbitmq"))

    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.codehaus.groovy:groovy")
    testImplementation(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}