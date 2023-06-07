package net.nemerosa.ontrack.gradle.extension

import com.github.gradle.node.NodeExtension
import com.github.gradle.node.NodePlugin
import com.github.gradle.node.npm.task.NpmInstallTask
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

class OntrackExtensionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.logger.info("[ontrack] Applying INTERNAL Ontrack plugin to ${target.path}")

        /**
         * Project"s configuration
         */

        val ontrack = target.extensions.create<OntrackExtension>("ontrack")

        /**
         * Node plugin setup
         */

        target.apply<NodePlugin>()
        target.configure<NodeExtension> {
            version.set("20.2.0")
            npmVersion.set("9.6.6")
            download.set(true)
        }

        /**
         * Project configuration
         */

        target.extra["ontrackWebDir"] = target.file(".gradle/ontrack")
        target.extra["ontrackWebNodeModulesDir"] = target.file("node_modules")

        /**
         * NPM tasks
         */

        val copyPackageJson by target.tasks.registering {
            doLast {
                logger.info("[ontrack] Copies the package.json file to ${target.projectDir}")
                OntrackExtensionPlugin::class.java.getResourceAsStream("/extension/package.json")!!.copyTo(
                        target.file("package.json").outputStream()
                )
            }
        }

        val copyGulpFile by target.tasks.registering {
            doLast {
                logger.info("[ontrack] Copies the gulpfile.js file to ${target.projectDir}")
                OntrackExtensionPlugin::class.java.getResourceAsStream("/extension/gulpfile.js")!!.copyTo(
                        target.file("gulpfile.js").outputStream()
                )
            }
        }

        target.tasks.named<NpmInstallTask>("npmInstall") {
            dependsOn(copyPackageJson)
            workingDir.set(target.projectDir)
            inputs.file(target.file("package.json"))
            outputs.dir(target.file("node_modules"))
        }

        /**
         * Gulp call
         */

        val web by target.tasks.registering(NpmTask::class) {
            dependsOn(target.tasks.named("npmInstall"))
            dependsOn(copyGulpFile)

            if (target.file("src/main/resources/static").exists()) {
                inputs.dir(target.file("src/main/resources/static"))
            }
            outputs.file(target.file("build/web/dist/module.js"))

            doFirst {
                target.mkdir(target.buildDir)
                logger.info("[ontrack] Generating web resources of ${ontrack.id(project)} in ${target.buildDir}")
            }

            workingDir.set(target.projectDir)
            environment.putAll(
                mapOf(
                    "EXTENSION" to ontrack.id(project),
                    "VERSION" to target.version.toString(),
                    "SRC" to target.file("src/main/resources/static").absolutePath,
                    "TARGET" to target.buildDir.absolutePath,
                )
            )
            args.set(listOf("run", "web"))
        }

        /**
         * Version file
         */

        val ontrackProperties by target.tasks.registering {
            description = "Prepares the ontrack META-INF file"
            doLast {
                target.mkdir("build")
                target.file("build/ontrack.properties").writeText("""
                    # Ontrack extension properties
                    version = ${target.version}
                    """.trimIndent()
                )
            }
        }

        /**
         * Update of the JAR task
         */

        target.tasks.named<Jar>("jar") {
            dependsOn(web)
            dependsOn(ontrackProperties)
            from("build/web/dist") {
                into("static/extension/${ontrack.id(project)}/${target.version}/")
            }
            from("build") {
                include("ontrack.properties")
                into("META-INF/ontrack/extension/")
                rename { "${ontrack.id(project)}.properties" }
            }
            exclude("static/**/*.js")
            // exclude("static/**/*.html")
        }

    }

}
