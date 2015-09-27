package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker start class
 */
class DockerStart extends AbstractDocker {

    String image = 'nemerosa/ontrack:latest'

    File data

    File conf

    String profile = 'prod'

    boolean exposePort = false

    String containerName

    boolean restart = false

    private String cid

    private int port

    @TaskAction
    def start() {
        // Arguments
        List<String> arguments = getDockerConfig()
        // Logging
        println "[${name}] Starting ${image} for profile ${profile}..."
        // All arguments
        arguments.addAll(['run', '--detach', "--env=PROFILE=${profile}"])
        // Port publication
        String portPublication
        if (exposePort) {
            println "[${name}] Publishing port 443..."
            arguments << "--publish=443:443"
        } else {
            println "[${name}] Publishing on random ports..."
            arguments << '--publish-all'
        }
        // Restart policy
        if (restart) {
            println "[${name}] Restarting on host restart"
            arguments << '--restart=always'
        }
        // Volumes
        if (data) {
            println "[${name}] Data mount: ${data}"
            arguments << "--volume=${data}:/var/ontrack/data"
        } else {
            println "[${name}] No data mount"
        }
        if (conf) {
            println "[${name}] Conf mount: ${conf}"
            arguments << "--volume=${conf}:/var/ontrack/conf"
        } else {
            println "[${name}] No conf mount"
        }
        // Container name
        if (containerName) {
            arguments << "--name=${containerName}"
        }
        // Image to start
        arguments << image
        // Starting the container
        cid = execute('docker', arguments)
        println "[${name}] Container ${cid} started"
        // Getting the published port
        this.port = getPublishedPort(this.cid)
        println "[${name}] Application running on port ${port}"
        project.ext.acceptanceOntrackPort = this.port
    }

    String getCid() {
        cid
    }

    int getPort() {
        port
    }
}