import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

description = "Search based on Elasticsearch"

apply<net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin>()

dependencies {
    implementation(project(":ontrack-extension-support"))
    implementation("io.searchbox:jest")
}
