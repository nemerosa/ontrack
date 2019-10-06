import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
}

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-support"))
    compile("org.springframework.security:spring-security-ldap")

    testCompile(project(":ontrack-it-utils"))
    testCompile("org.codehaus.groovy:groovy")
    testRuntime(project(":ontrack-service"))
    testRuntime(project(":ontrack-repository-impl"))
}
