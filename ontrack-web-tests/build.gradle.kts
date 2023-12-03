import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node")
}

// Node environment

configure<NodeExtension> {
    version.set("20.2.0")
    npmVersion.set("9.6.6")
    download.set(true)
}

// Test environment

val setupTestEnvironment = tasks.getByPath(":localComposeUp")

val tearDownTestEnvironment = tasks.getByPath(":localComposeDown")

// Testing

val uiTest by tasks.registering(NpmTask::class) {
    dependsOn("npmInstall")
    dependsOn(setupTestEnvironment)
    finalizedBy(tearDownTestEnvironment)

    args.set(listOf("run", "test"))
}

