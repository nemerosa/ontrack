import com.liferay.gradle.plugins.node.NodeExtension
import com.liferay.gradle.plugins.node.tasks.ExecuteNodeScriptTask
import com.liferay.gradle.plugins.node.tasks.NpmInstallTask

plugins {
    base
    id("com.liferay.node")
}

// Node environment

configure<NodeExtension> {
    isGlobal = true
    setNodeVersion("8.10.0")
    setNpmVersion("5.7.1")
    isDownload = true
}

// NPM cache

tasks.named<NpmInstallTask>("npmInstall") {
    setNodeModulesCacheDir("$rootDir/.gradle/node_modules_cache")
    inputs.file("package.json")
    outputs.dir("node_modules")
}

// Installing the Bower packages

val bowerInstall by tasks.registering(ExecuteNodeScriptTask::class) {
    dependsOn("npmInstall")
    setScriptFile("node_modules/bower/bin/bower")
    val bowerArgs = mutableListOf(
            "--config.storage.cache=$rootDir/.gradle/bower/cache",
            "--config.storage.packages=$rootDir/.gradle/bower/packages",
            "--config.storage.registry=$rootDir/.gradle/bower/registry",
            "install"
    )
    if (project.properties.containsKey("bowerOptions")) {
        bowerArgs += project.properties["bowerOptions"].toString()
    }
    args(bowerArgs)
    inputs.file("bower.json")
    outputs.dir("vendor")
}

// Cleanup

tasks.named<Delete>("clean") {
    delete("build/web")
}

// Web packaging

val dev by tasks.registering(ExecuteNodeScriptTask::class) {
    dependsOn(bowerInstall)
    setScriptFile("node_modules/gulp/bin/gulp")
    args("dev")
}

val prod by tasks.registering(ExecuteNodeScriptTask::class) {
    dependsOn(bowerInstall)
    setScriptFile("node_modules/gulp/bin/gulp")
    args("default", "--version", version)
    inputs.dir("src")
    inputs.file("bower.json")
    inputs.file("package.json")
    outputs.dir("build/web/prod")
}

tasks.named<Task>("build") {
    dependsOn(prod)
}

val watch by tasks.registering(ExecuteNodeScriptTask::class) {
    dependsOn(dev)
    setScriptFile("node_modules/gulp/bin/gulp")
    args("watch")
}
