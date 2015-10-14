package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Copies a file to a container
 */
class DockerCopy extends AbstractContainerDocker {

    String sourcePath
    String containerPath

    @TaskAction
    def run() {
        // Gets the container id or name
        String container = getContainer()
        // Runs the command
        docker 'cp', sourcePath, "${container}:${containerPath}"
    }

}
