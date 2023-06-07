import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.task.NodeTask

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

// Installing the Bower packages

val bowerInstall by tasks.registering(NodeTask::class) {
    dependsOn("npmInstall")
    script.set(file("node_modules/bower/bin/bower"))
    val bowerArgs = mutableListOf(
        "--config.storage.cache=$rootDir/.gradle/bower/cache",
        "--config.storage.packages=$rootDir/.gradle/bower/packages",
        "--config.storage.registry=$rootDir/.gradle/bower/registry",
        "install"
    )
    if (project.properties.containsKey("bowerOptions")) {
        bowerArgs += project.properties["bowerOptions"].toString()
    }
    args.set(bowerArgs)
    inputs.file("bower.json")
    outputs.dir("vendor")
}

// Cleanup

tasks.named<Delete>("clean") {
    delete("build/web")
}

// Web packaging

val dev by tasks.registering(NpmTask::class) {
    dependsOn(bowerInstall)
    args.set(listOf("run", "dev"))
}

val prod by tasks.registering(NpmTask::class) {
    dependsOn(bowerInstall)
    environment.put("VERSION", version.toString())
    args.set(
        listOf("run", "prod")
    )
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
    script.set(file("node_modules/gulp/bin/gulp.js"))
    args.set(listOf("watch"))
}
