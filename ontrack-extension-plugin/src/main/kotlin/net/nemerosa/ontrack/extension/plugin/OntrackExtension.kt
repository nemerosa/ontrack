package net.nemerosa.ontrack.extension.plugin

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Prefix for Ontrack extensions module names
 */
const val PREFIX = "ontrack-extension-"

/**
 * Configuration of the Ontrack extension.
 *
 * @property project Linked project
 */
open class OntrackExtension(
        private val project: Project
) {

    /**
     * ID of the extension (required)
     */
    private var id: String? = null

    /**
     * DSL access
     */
    fun id(value: String) {
        id = value
    }

    /**
     * Applies Kotlin dependencies
     */
    fun kotlin() {
        val kotlinVersion = project.extra["kotlinVersion"] as String
        println("[ontrack] Applying Kotlin v${kotlinVersion} to ${project.name} plugin")
        project.apply(plugin = "kotlin")
        project.apply(plugin = "kotlin-spring")
        project.tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
        project.dependencies {
            "compileOnly"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
            "compileOnly"("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
        }
    }

    /**
     * Dynamic computation of the ID if not specified
     */
    fun id(): String {
        return id ?: if (project.name.startsWith(PREFIX)) {
            project.name.removePrefix(PREFIX)
        } else {
            throw GradleException("""
                Project ${project.path} must declare the Ontrack extension id or have a name like `ontrack-extension-<id>`.
                
                Use:
                
                ontrack {
                   id "your-extension-id"
                }
                """.trimIndent())
        }
    }

    /**
     * Registers an ontrack core extension
     */
    fun uses(extension: String) {
        val version = project.extra["ontrackVersion"] as String
        project.dependencies {
            "compile"("net.nemerosa.ontrack:ontrack-extension-${extension}:${version}")
        }
    }

}
