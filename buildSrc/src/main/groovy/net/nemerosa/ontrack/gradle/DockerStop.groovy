package net.nemerosa.ontrack.gradle

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Docker stop task
 */
class DockerStop extends AbstractDocker {

    String startTask
    String containerName
    String logFile = 'build/docker.log'
    boolean remove = true
    boolean removeVolumes = true
    boolean ignoreError = false

    protected String getContainer() {
        if (startTask) {
            // Gets the start task
            def task = project.tasks.getByName(startTask) as DockerStart
            // Stopping the container
            println "[${name}] Stopping container at ${task.cid} created by ${startTask}"
            // OK
            return task.cid
        } else if (containerName) {
            println "[${name}] Stopping container ${containerName}"
            return containerName
        } else {
            throw new GradleException("Either `startTask` or `containerName` must be specified.")
        }
    }

    @TaskAction
    def stop() {
        // Gets the container id or name
        String container = getContainer()
        // Getting all the logs
        if (logFile) {
            println "[${name}] Writing log files of ${container} at ${logFile}..."
            String logs = docker('logs', container)
            def targetFile = project.file(logFile)
            targetFile.parentFile.mkdirs()
            targetFile.text = logs
        }
        // Arguments
        List<String> arguments = getDockerConfig()
        // Stopping
        if (remove) {
            arguments << 'rm'
            arguments << '--force'
            if (removeVolumes) {
                arguments << '--volumes'
            }
        } else {
            arguments << 'stop'
        }
        // Container name
        arguments << container
        // Running the command
        project.exec {
            executable 'docker'
            args = arguments
            ignoreExitValue = ignoreError
        }
    }

}
