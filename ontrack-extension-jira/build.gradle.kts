import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-issues"))

    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-notifications"))
    implementation(project(":ontrack-tx"))
    implementation("org.springframework:spring-tx")
    implementation("org.apache.commons:commons-lang3")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-scm"))
    testImplementation(project(":ontrack-extension-recordings"))
    testImplementation(project(path = ":ontrack-extension-support", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-scm", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-notifications", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-queue", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-recordings", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}