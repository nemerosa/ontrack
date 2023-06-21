import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node")
}

val webInstall by tasks.registering(NpmTask::class) {
    args.set(listOf("install"))
}

val webBuild by tasks.registering(NpmTask::class) {
    dependsOn(webInstall)
    args.set(listOf("run", "build"))
}

tasks.named("build") {
    dependsOn(webBuild)
}