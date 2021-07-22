import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-extension-indicators"))
    implementation(project(":ontrack-extension-scm"))

    implementation("org.apache.commons:commons-lang3")

    testImplementation("org.codehaus.groovy:groovy")
    testImplementation(project(":ontrack-test-utils"))
}