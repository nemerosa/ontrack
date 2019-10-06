import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
}

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-support"))
    compile("org.springframework:spring-tx")

    testCompile("org.codehaus.groovy:groovy")
    testCompile(project(":ontrack-test-utils"))
}