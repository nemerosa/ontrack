import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

plugins {
    groovy
}

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-scm"))
    compile(project(":ontrack-ui-graphql"))
    compile(project(":ontrack-git"))
    compile(project(":ontrack-tx"))
    compile(project(":ontrack-json"))
    compile(project(":ontrack-repository-support"))
    compile("org.springframework:spring-tx")
    compile("commons-io:commons-io")

    testCompile(project(":ontrack-it-utils"))
    testCompile(project(path = ":ontrack-extension-api", configuration = "tests"))
    testCompile(project(path = ":ontrack-extension-issues", configuration = "tests"))
    testCompile(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testCompile("org.codehaus.groovy:groovy")
    testRuntime(project(":ontrack-service"))
    testRuntime(project(":ontrack-repository-impl"))
    testRuntime("org.springframework.boot:spring-boot-starter-web")
}
