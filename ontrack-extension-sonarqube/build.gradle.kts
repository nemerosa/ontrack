import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-extension-casc"))
    implementation("io.micrometer:micrometer-core")
    implementation(project(":ontrack-extension-indicators"))
    implementation("org.slf4j:slf4j-api")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
