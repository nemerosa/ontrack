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

    /**
     * Collection of all container logs
     */
    String logs

    /**
     * Service to logs (optional)
     */
    String logService

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

        // Collection of logs
        if (logs) {
            File logDir = project.file(logs)
            if (!logDir.exists()) {
                project.mkdir(logDir)
            }
            List<String> logArgs = [
                    'logs',
                    '--no-color',
                    '--timestamps',
            ]
            if (logService) {
                logArgs << logService
            }
            String logAsText = compose(logArgs as Object[])
            def logFile = new File(logDir, 'compose.log')
            println "[${name}] Putting ${logService} log into ${logFile}"
            logFile.text = logAsText
        }

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
