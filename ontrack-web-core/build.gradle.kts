import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node")
    id("com.bmuschko.docker-remote-api")
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

val test by tasks.registering(NpmTask::class) {
    dependsOn("npmInstall")
    args.set(listOf("run", "test"))
}

tasks.named("build") {
    dependsOn(webBuild)
    dependsOn(test)
}

// Docker image

val dockerBuild by tasks.registering(DockerBuildImage::class) {
    inputDir.set(project.projectDir)
    images.add("nemerosa/ontrack-ui:$version")
    images.add("nemerosa/ontrack-ui:latest")
}
