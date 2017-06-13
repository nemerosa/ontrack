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
     * Applies Kotlin dependencies?
     */
    boolean kotlin = false

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
        this.kotlin = true
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
