import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

val slackVersion = "1.29.2"

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-ui-support"))

    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-notifications"))
    implementation("org.slf4j:slf4j-api")
    implementation("com.slack.api:slack-api-client:${slackVersion}")

    testImplementation(project(path = ":ontrack-ui-support", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
