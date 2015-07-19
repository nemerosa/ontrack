package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker stop task
 */
class DockerStop extends AbstractDocker {

    String startTask
    String logFile = 'build/docker.log'

    @TaskAction
    def stop() {
        // Gets the start task
        def task = project.tasks.getByName(startTask) as DockerStart
        // Stopping the container
        println "[${name}] Stopping container at ${task.cid} created by ${startTask}"
        // Arguments
        List<String> arguments = getDockerConfig()
        // Getting all the logs
        println "[${name}] Writing log files of ${task.cid} at ${logFile}..."
        String logs = docker('logs', task.cid)
        def targetFile = project.file(logFile)
        targetFile.parentFile.mkdirs()
        targetFile.text = logs
        // Stopping
        arguments.addAll(['rm', '--force', '--volumes', task.cid])
        project.exec {
            executable 'docker'
            args = arguments
        }
    }

}
