package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Runs a command in a container
 */
class DockerExec extends AbstractContainerDocker {

    String[] commands

    @TaskAction
    def run() {
        // Gets the container id or name
        String container = getContainer()
        // Arguments
        List<String> arguments = [
                'exec', '--tty', container
        ]
        // Commands
        arguments.addAll commands
        // Runs the command
        docker(arguments as String[])
    }

}
