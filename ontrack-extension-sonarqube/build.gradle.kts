import net.nemerosa.ontrack.gradle.extension.OntrackExtensionPlugin

apply<OntrackExtensionPlugin>()

dependencies {
    compile(project(":ontrack-extension-support"))

    testCompile(project(":ontrack-it-utils"))
    testCompile(project(":ontrack-extension-general"))
    testCompile(project(path = ":ontrack-model", configuration = "tests"))
    testCompile(project(path = ":ontrack-extension-api", configuration = "tests"))

    testRuntime(project(":ontrack-service"))
    testRuntime(project(":ontrack-repository-impl"))
}
