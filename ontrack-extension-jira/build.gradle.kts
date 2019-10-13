import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-issues"))

    implementation(project(":ontrack-tx"))
    implementation("org.springframework:spring-tx")
    implementation("org.apache.commons:commons-lang3")

    testImplementation("org.codehaus.groovy:groovy")
    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}