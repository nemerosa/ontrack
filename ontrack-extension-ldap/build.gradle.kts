import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-extension-casc"))
    implementation("org.springframework.security:spring-security-ldap")
    implementation("org.apache.commons:commons-lang3")
    implementation("javax.validation:validation-api")

    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
    testImplementation("com.unboundid:unboundid-ldapsdk:4.0.14")
}
