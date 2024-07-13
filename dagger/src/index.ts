/**
 * A generated module for Dagger functions
 *
 * This module has been generated via dagger init and serves as a reference to
 * basic module structure as you get started with Dagger.
 *
 * Two functions have been pre-created. You can modify, delete, or add to them,
 * as needed. They demonstrate usage of arguments and return types using simple
 * echo and grep commands. The functions can be called from the dagger CLI or
 * from one of the SDKs.
 *
 * The first line in this comment block is a short description line and the
 * rest is a long description with more detail on the module's purpose or usage,
 * if appropriate. All modules should have a short description.
 */
import {dag, Container, Directory, object, func} from "@dagger.io/dagger"

@object()
class Dagger {

    /**
     * Building the application
     */
    @func()
    async build(source: Directory) {
        // Building the Spring Boot application
        return this.buildEnv(source)
            .withExec([
                "./gradlew",
                "assemble", // TODO Replaces with build
                "-PbowerOptions='--allow-root'",
                "-Dorg.gradle.jvmargs=-Xmx6144m",
                "--stacktrace",
                "--parallel",
                "--no-daemon"
            ])
            .directory("./")
    }

    /**
     * Getting the version of the application
     */
    @func()
    async version(source: Directory): Promise<string> {
        return this.buildEnv(source)
            .withExec(["./gradlew", "versionDisplay", "versionFile", "--no-daemon"])
            .stdout()
    }

    /**
     * Build environment
     */
    @func()
    buildEnv(source: Directory): Container {
        return dag
            .container()
            .from("azul/zulu-openjdk:17.0.6")
            .withDirectory("/src", source)
            .withWorkdir("/src")
            .withMountedCache(
                "/src/.gradle",
                dag.cacheVolume("gradle-app"),
            )
            .withMountedCache(
                "/root/.gradle",
                dag.cacheVolume("gradle-global")
            )
    }

}