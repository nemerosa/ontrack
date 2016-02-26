package net.nemerosa.ontrack.extension.plugin

import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Configuration of the Ontrack extension.
 */
class OntrackExtension {

    static final String PREFIX = 'ontrack-extension-'

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
     * Dynamic computation of the ID if not specified
     */
    String id(Project project) {
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


}
