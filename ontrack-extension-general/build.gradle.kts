import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
}

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-support"))

    testCompile("org.codehaus.groovy:groovy")
    testCompile(project(":ontrack-it-utils"))
    testRuntime(project(":ontrack-service"))
    testRuntime(project(":ontrack-repository-impl"))
}
