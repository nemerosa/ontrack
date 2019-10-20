import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    implementation(project(":ontrack-extension-issues"))
    implementation("org.apache.commons:commons-lang3")

    testImplementation(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testImplementation("org.codehaus.groovy:groovy")
}
