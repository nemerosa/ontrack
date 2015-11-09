package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker Compose `stop` command
 */
class ComposeStop extends AbstractCompose {

    /**
     * Timeout in seconds
     */
    int timeout = 0

    /**
     * Removing the containers after stopping?
     */
    boolean remove = false

    /**
     * Service to stop/remove (optional)
     */
    String service

    @TaskAction
    def run() {
        // Arguments
        List<?> args = []
        // Command
        args << 'stop'
        // Timeout
        if (timeout > 0) {
            args << '--timeout'
            args << timeout
        }
        // Service?
        if (service) {
            args << service
        }
        // Running
        compose(args as Object[])

        // Removing?
        if (remove) {
            args = ['rm', '-f']
            if (service) {
                args << service
            }
            compose(args as Object[])
        }
    }

}
