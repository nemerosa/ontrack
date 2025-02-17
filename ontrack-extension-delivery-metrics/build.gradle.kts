import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-extension-chart"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-git"))
    implementation(project(":ontrack-ui-graphql"))
    implementation("org.apache.commons:commons-math3")
    implementation(project(":ontrack-repository-support"))

    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation("com.networknt:json-schema-validator")
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
