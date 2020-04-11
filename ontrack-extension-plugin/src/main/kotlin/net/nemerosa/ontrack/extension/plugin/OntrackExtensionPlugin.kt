package net.nemerosa.ontrack.extension.plugin

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import java.util.*

/**
 * Plugin to create, manage, package and test an Ontrack extension.
 */
class OntrackExtensionPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        /**
         * Reading the version information
         */

        val properties = Properties()
        OntrackExtensionPlugin::class.java.getResourceAsStream("/META-INF/gradle-plugins/ontrack.properties").use { properties.load(it) }
        val ontrackVersion = properties.getProperty("implementation-version")
        val theKotlinVersion = properties.getProperty("kotlin-version")
        project.extra["ontrackVersion"] = ontrackVersion

        println("[ontrack] Applying Ontrack plugin v${ontrackVersion} to ${project.name}")

        /**
         * Kotlin version
         */

        project.extra["kotlinVersion"] = theKotlinVersion

        /**
         * Java project
         */

        project.apply(plugin = "java")
        project.apply(plugin = "java-library")

        /**
         * Default dependencies
         */

        val runtime = project.configurations.getByName("runtime")

        val ontrack = project.configurations.create("ontrack") {
            extendsFrom(runtime)
        }

        project.dependencies {
            "implementation"("net.nemerosa.ontrack:ontrack-extension-support:${ontrackVersion}")

            "testImplementation"("net.nemerosa.ontrack:ontrack-it-utils:${ontrackVersion}")
            "testRuntimeOnly"("net.nemerosa.ontrack:ontrack-service:${ontrackVersion}")
            "testRuntimeOnly"("net.nemerosa.ontrack:ontrack-repository-impl:${ontrackVersion}")

            ontrack("net.nemerosa.ontrack:ontrack-ui:${ontrackVersion}")
        }

        /**
         * Project"s configuration
         */

        val ontrackExtension = project.extensions.create("ontrack", OntrackExtension::class.java, project)

        /**
         * Node plugin setup
         */

        project.apply<NodePlugin>()
        project.configure<NodeExtension> {
            version = "8.10.0"
            npmVersion = "5.7.1"
            isDownload = true
        }

        /**
         * NPM tasks
         */

        val copyPackageJson by project.tasks.registering {
            doLast {
                println("[ontrack] Copies the package.json file to ${project.projectDir}")
                project.file("package.json").outputStream().use {
                    OntrackExtensionPlugin::class.java.getResourceAsStream("/extension/package.json").copyTo(it)
                }
            }
        }

        val copyGulpFile by project.tasks.registering {
            doLast {
                println("[ontrack] Copies the gulpfile.js file to ${project.projectDir}")
                project.file("gulpfile.js").outputStream().use {
                    OntrackExtensionPlugin::class.java.getResourceAsStream("/extension/gulpfile.js").copyTo(it)
                }
            }
        }

        /**
         * Gulp call
         */

        val web by project.tasks.registering(NodeTask::class) {
            dependsOn("npmInstall")
            dependsOn(copyGulpFile)
            dependsOn(copyPackageJson)

            if (project.file("src/main/resources/static").exists()) {
                inputs.dir(project.file("src/main/resources/static"))
            }
            outputs.file(project.file("build/web/dist/module.js"))

            doFirst {
                project.mkdir(project.buildDir)
                println("[ontrack] Generating web resources of ${ontrackExtension.id()} in ${project.buildDir}")
            }

            script = project.file("node_modules/gulp/bin/gulp.js")
            setArgs(listOf(
                    "default",
                    "--extension", ontrackExtension.id(),
                    "--version", project.version,
                    "--src", project.file("src/main/resources/static"),
                    "--target", project.buildDir
            ))
        }

        /**
         * Version file
         */

        val ontrackProperties by project.tasks.registering {
            description = "Prepares the ontrack META-INF file"
            doLast {
                project.file("build/ontrack.properties").writeText("""
                    # Ontrack extension properties
                    version = ${project.version}
                    ontrack = $ontrackVersion
                    """.trimIndent())
            }
        }

        /**
         * Update of the JAR task
         */

        val jar by project.tasks.named("jar", Jar::class) {
            dependsOn(web)
            dependsOn(ontrackProperties)
            from("build/web/dist") {
                into { "static/extension/${ontrackExtension.id()}/${project.version}/" }
            }
            from("build") {
                include("ontrack.properties")
                into("META-INF/ontrack/extension/")
                rename { "${ontrackExtension.id()}.properties" }
            }
            exclude("static/**/*.js")
            exclude("static/**/*.html")
        }

        /**
         * Custom configuration to package a module"s own dependencies, minus the ones
         * provided by Ontrack itself.
         */

        val moduleDependencies = project.configurations.create("moduleDependencies") {
            extendsFrom(runtime)
            exclude(group = "net.nemerosa.ontrack")
            exclude(group = "org.springframework")
            exclude(group = "org.apache.httpcomponents")
            exclude(group = "org.codehaus.groovy")
            exclude(group = "org.projectlombok")
            exclude(group = "org.slf4j")
            exclude(group = "commons-io")
        }

        /**
         * Task to copy the dependencies
         */

        val ontrackCopyDependencies by project.tasks.registering(Copy::class) {
            from(moduleDependencies)
            into("build/dist")
        }

        /**
         * Task to prepare dependencies & main JAR in dist folder
         */

        val ontrackDist by project.tasks.registering(Copy::class) {
            dependsOn(ontrackCopyDependencies)
            from(jar)
            into("build/dist")
        }

        /**
         * Building launches the `ontrackDist` task
         */

        project.tasks.named("build") {
            dependsOn(ontrackDist)
        }

        /**
         * Running the extension in Ontrack
         */

        @Suppress("UNUSED_VARIABLE")
        val ontrackRun by project.tasks.registering(JavaExec::class) {
            val sourceSets = project.extensions.getByName<SourceSetContainer>("sourceSets")
            classpath = ontrack + sourceSets.getByName("main").runtimeClasspath
            // Using explicitely the Spring Boot launcher
            main = "org.springframework.boot.loader.PropertiesLauncher"
            systemProperties = mapOf(
                    "loader.main" to "net.nemerosa.ontrack.boot.Application"
            )
        }
    }

}
