import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
}

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-issues"))
    compile(project(":ontrack-tx"))
    compile("org.springframework:spring-tx")

    testCompile("org.codehaus.groovy:groovy")
    testCompile(project(":ontrack-it-utils"))
    testCompile(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testRuntime(project(":ontrack-service"))
    testRuntime(project(":ontrack-repository-impl"))
}