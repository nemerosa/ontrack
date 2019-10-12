import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
}

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-scm"))
    compile(project(":ontrack-repository-support"))
    compile(project(":ontrack-ui-graphql"))
    compile(project(":ontrack-tx"))
    compile("org.springframework:spring-tx")
    compile("org.tmatesoft.svnkit:svnkit:1.8.12")

    testCompile(project(":ontrack-it-utils"))
    testCompile(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testCompile(project(path = ":ontrack-extension-api", configuration = "tests"))
    testCompile(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testCompile("org.codehaus.groovy:groovy")

    testRuntime(project(":ontrack-service"))
    testRuntime(project(":ontrack-repository-impl"))
}