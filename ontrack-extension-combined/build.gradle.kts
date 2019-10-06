import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
}

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-issues"))

    testCompile(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testCompile("org.codehaus.groovy:groovy")
}
