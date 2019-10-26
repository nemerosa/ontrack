import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
    `java-library`
}

apply<OntrackExtensionPlugin>()

dependencies {
    implementation(project(":ontrack-extension-support"))
    implementation("org.springframework.security:spring-security-ldap")
    implementation("org.apache.commons:commons-lang3")
    implementation("javax.validation:validation-api")

    testImplementation(project(":ontrack-it-utils"))
    testImplementation("org.codehaus.groovy:groovy")

    testRuntimeOnly(project(":ontrack-service"))
    testRuntimeOnly(project(":ontrack-repository-impl"))
}
