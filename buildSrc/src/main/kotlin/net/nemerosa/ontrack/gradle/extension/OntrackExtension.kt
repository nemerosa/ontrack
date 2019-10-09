package net.nemerosa.ontrack.gradle.extension

import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Configuration of the extension
 */
open class OntrackExtension {

    companion object {
        const val PREFIX = "ontrack-extension-"
    }

    /**
     * ID of the extension (required)
     */
    var id: String = ""

    /**
     * DSL access
     */
    fun id(value: String) {
        this.id = value
    }

    /**
     * Dynamic computation of the ID if not specified
     */
    fun id(project: Project): String {
        if (id.isNotBlank()) {
            return id
        } else if (project.name.startsWith(PREFIX)) {
            return project.name.removePrefix(PREFIX)
        } else {
            throw GradleException("""
                Project ${project.path} must declare the Ontrack extension id.
                
                Use:
                
                ontrack {
                   id 'your-extension-id'
                }
                """.trimIndent()
            )
        }
    }


}
