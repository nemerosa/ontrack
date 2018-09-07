package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker Compose `run` command
 */
class ComposeRun extends AbstractCompose {

    /**
     * Service to run the command on
     */
    String service

    /**
     * Command to run
     */
    String[] commands;

    @TaskAction
    def run() {
        // Arguments
        List<?> args = []
        // Command
        args << 'run'
        args << service
        args.addAll(commands)
        // Running
        compose(args as Object[])
    }

}
