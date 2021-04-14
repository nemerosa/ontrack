import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-ui-graphql"))
    implementation("org.apache.commons:commons-lang3")
    implementation(project(":ontrack-extension-casc"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation("org.codehaus.groovy:groovy")
    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
