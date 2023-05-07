import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-extension-hook"))
    implementation(project(":ontrack-extension-queue"))
    implementation(project(":ontrack-extension-casc"))
    implementation(project(":ontrack-extension-general"))
    implementation("io.micrometer:micrometer-core")
    implementation("org.slf4j:slf4j-api")
    implementation("jakarta.annotation:jakarta.annotation-api")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(path = ":ontrack-extension-queue", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-hook", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-recordings", configuration = "tests"))
    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
