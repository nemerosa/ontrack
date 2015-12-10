package net.nemerosa.ontrack.gradle.extension

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task which checks the project's configuration
 */
class OntrackCheck extends DefaultTask {

    @TaskAction
    def run() {
        // Checks that the project extension has an ID
        def ontrack = project.extensions.ontrack as OntrackExtension
        if (!ontrack.id) {
            throw new GradleException("The `id` in the `ontrack` extension is required.")
        }
        // Logging
        logger.info("Ontrack extension: ${ontrack.id}")
    }

}
