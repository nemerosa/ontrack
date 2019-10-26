import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    implementation(project(":ontrack-extension-support"))
    implementation("io.micrometer:micrometer-core")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
