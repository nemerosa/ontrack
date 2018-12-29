package net.nemerosa.ontrack.extension.plugin

import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Configuration of the Ontrack extension.
 */
class OntrackExtension {

    static final String PREFIX = 'ontrack-extension-'

    /**
     * Linked project
     */
    private final Project project

    /**
     * Constructor
     */
    OntrackExtension(Project project) {
        this.project = project
    }

    /**
     * ID of the extension (required)
     */
    String id

    /**
     * DSL access
     */
    void id(String value) {
        this.id = value
    }

    /**
     * Applies Kotlin dependencies
     */
    void kotlin() {
        def kotlinVersion = project.ext.kotlinVersion
        println "[ontrack] Applying Kotlin v${kotlinVersion} to ${project.name} plugin"
        project.apply plugin: 'kotlin'
        project.apply plugin: 'kotlin-spring'
        project.compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
        project.compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
        project.dependencies {
            compileOnly "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}"
            compileOnly "org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}"
        }
    }

    /**
     * Dynamic computation of the ID if not specified
     */
    String id() {
        if (this.id) {
            return id
        } else if (project.name.startsWith(PREFIX)) {
            return project.name - PREFIX
        } else {
            throw new GradleException("""\
Project ${project.path} must declare the Ontrack extension id or have a name like `ontrack-extension-<id>`.

Use:

ontrack {
   id 'your-extension-id'
}
""")
        }
    }

    /**
     * Registers an ontrack core extension
     */
    void uses(String extension) {
        def version = project.ontrackVersion as String
        project.dependencies {
            compile "net.nemerosa.ontrack:ontrack-extension-${extension}:${version}"
        }
    }

}
