package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Runs a command in a container
 */
class DockerExec extends AbstractContainerDocker {

    def commands

    private Map<String, String> logs = [:]

    @TaskAction
    def run() {
        // Gets the container id or name
        String container = getContainer()
        // List of command sets
        Map<String, String[]> commandSet = [:]
        // String only
        if (commands instanceof String) {
            commandSet = [
                    main: (commands as String).split(' '),
            ]
        } else if (commands instanceof String[]) {
            commandSet = [
                    main: (commands as String[])
            ]
        } else if (commands instanceof Collection) {
            commandSet = [
                    main: (commands as String[])
            ]
        } else if (commands instanceof Map) {
            commandSet = commands as Map
        } else {
            throw new IllegalArgumentException("The `commands` must be a list of commands or a map of command lists")
        }
        // For each command set
        println "[${name}] Executing commands..."
        commandSet.each { name, arguments ->
            logs.put name, runInContainer(container, name, arguments as String[])
        }
    }

    protected String runInContainer(String container, String commandName, String[] arguments) {
        println "[${name}] Running ${commandName} commands"
        // Arguments
        List<String> dockerArguments = [
                'exec', '--tty', container
        ]
        // Commands
        dockerArguments.addAll arguments
        // Running
        return docker(dockerArguments as String[])
    }

    Map<String, String> getLogs() {
        return logs
    }
}
