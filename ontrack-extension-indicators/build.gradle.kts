import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-ui-graphql"))

    implementation(project(":ontrack-repository-support"))
    implementation("io.micrometer:micrometer-core")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-support", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
