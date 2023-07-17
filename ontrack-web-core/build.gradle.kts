import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node")
}

// Node environment

configure<NodeExtension> {
    version.set("20.2.0")
    npmVersion.set("9.6.6")
    download.set(true)
}

val webBuild by tasks.registering(NpmTask::class) {
    dependsOn("npmInstall")
    args.set(listOf("run", "build"))
}

tasks.named("build") {
    dependsOn(webBuild)
}