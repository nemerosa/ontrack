import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
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
    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-git"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-git", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}