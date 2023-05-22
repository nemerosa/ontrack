import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    implementation(project(":ontrack-extension-issues"))
    implementation("org.apache.commons:commons-lang3")

    testImplementation(project(path = ":ontrack-extension-issues", configuration = "tests"))
}
