dependencies {
    compile(project(":ontrack-extension-api"))
    compile(project(":ontrack-client"))
    compile(project(":ontrack-ui-support"))

    // Make sure the following libraries are available for the extension when they need them
    runtime(project(":ontrack-git"))
    runtime(project(":ontrack-tx"))
}