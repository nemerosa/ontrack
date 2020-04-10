import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.npm.NpmInstallTask
import com.moowork.gradle.node.task.NodeTask

plugins {
    base
    id("com.github.node-gradle.node")
}

// Node environment

configure<NodeExtension> {
    version = "8.10.0"
    npmVersion = "5.7.1"
    isDownload = true
}

// Installing the Bower packages

val bowerInstall by tasks.registering(NodeTask::class) {
    dependsOn("npmInstall")
    script = file("node_modules/bower/bin/bower")
    val bowerArgs = mutableListOf(
            "--config.storage.cache=$rootDir/.gradle/bower/cache",
            "--config.storage.packages=$rootDir/.gradle/bower/packages",
            "--config.storage.registry=$rootDir/.gradle/bower/registry",
            "install"
    )
    if (project.properties.containsKey("bowerOptions")) {
        bowerArgs += project.properties["bowerOptions"].toString()
    }
    setArgs(bowerArgs)
    inputs.file("bower.json")
    outputs.dir("vendor")
}

// Cleanup

tasks.named<Delete>("clean") {
    delete("build/web")
}

// Web packaging

val dev by tasks.registering(NodeTask::class) {
    dependsOn(bowerInstall)
    script = file("node_modules/gulp/bin/gulp.js")
    addArgs("dev")
}

val prod by tasks.registering(NodeTask::class) {
    dependsOn(bowerInstall)
    script = file("node_modules/gulp/bin/gulp.js")
    addArgs("default", "--version", version)
    inputs.dir("src")
    inputs.file("bower.json")
    inputs.file("package.json")
    outputs.dir("build/web/prod")
}

tasks.named<Task>("build") {
    dependsOn(prod)
}

val watch by tasks.registering(NodeTask::class) {
    dependsOn(dev)
    script = file("node_modules/gulp/bin/gulp.js")
    addArgs("watch")
}
