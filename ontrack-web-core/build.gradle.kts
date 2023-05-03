import com.moowork.gradle.node.npm.NpmTask

plugins {
    base
    id("com.github.node-gradle.node")
}

val webInstall by tasks.registering(NpmTask::class) {
    setArgs(listOf("install"))
}

val webBuild by tasks.registering(NpmTask::class) {
    dependsOn(webInstall)
    setArgs(listOf("run", "build"))
}

tasks.named("build") {
    dependsOn(webBuild)
}