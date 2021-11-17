import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    `java-library`
}

apply(plugin = "org.springframework.boot")
apply(plugin = "com.bmuschko.docker-remote-api")

dependencies {
    testImplementation(project(":ontrack-json"))
    testImplementation("org.springframework.boot:spring-boot-starter")
    testImplementation(project(":ontrack-kdsl-http"))
    testImplementation(project(path = ":ontrack-extension-github", configuration = "kdsl"))
    testImplementation("commons-io:commons-io")
}

/**
 * Packaging
 */

val bootJar = tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    bootInf {
        from(sourceSets["test"].output)
        into("classes")
    }
    classpath(configurations.named("testRuntimeClasspath"))
    mainClass.set("net.nemerosa.ontrack.kdsl.acceptance.app.Start")
}

val normaliseJar by tasks.registering(Copy::class) {
    dependsOn(bootJar)
    from("$buildDir/libs/")
    include("ontrack-kdsl-acceptance-$version.jar")
    into("$buildDir/libs/")
    rename("ontrack-kdsl-acceptance-$version.jar", "ontrack-kdsl-acceptance.jar")
}

val acceptanceDockerPrepareEnv by tasks.registering(Copy::class) {
    dependsOn(normaliseJar)
    from("${buildDir}/libs/ontrack-kdsl-acceptance.jar")
    into("${projectDir}/src/main/docker")
}

tasks.named("assemble") {
    dependsOn(normaliseJar)
}

val dockerBuild by tasks.registering(DockerBuildImage::class) {
    dependsOn(acceptanceDockerPrepareEnv)
    inputDir.set(file("src/main/docker"))
    images.add("nemerosa/ontrack-kdsl-acceptance:$version")
    images.add("nemerosa/ontrack-kdsl-acceptance:latest")
}
