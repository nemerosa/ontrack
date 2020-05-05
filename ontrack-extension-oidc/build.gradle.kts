import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))
    api(project(":ontrack-ui-support"))

    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.slf4j:slf4j-api")

    implementation("org.springframework.security:spring-security-oauth2-client")
    runtimeOnly("org.springframework.security:spring-security-oauth2-jose")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation("com.squareup.okhttp3:mockwebserver")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
