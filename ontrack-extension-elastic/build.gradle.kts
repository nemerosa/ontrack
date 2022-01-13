import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
