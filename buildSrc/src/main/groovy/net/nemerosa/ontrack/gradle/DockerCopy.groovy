package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Copies a file to a container
 */
class DockerCopy extends AbstractContainerDocker {

    Map<String, String> copies = [:]

    @TaskAction
    def run() {
        // Gets the container id or name
        String container = getContainer()
        // Runs the copies
        copies.each { sourcePath, containerPath ->
            docker 'cp', sourcePath, "${container}:${containerPath}"
        }
    }

}
