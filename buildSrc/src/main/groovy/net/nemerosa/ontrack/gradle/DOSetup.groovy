package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * DO environment
 */
class DOSetup extends AbstractCDTask {

    String apiToken
    String dropletName
    String region = 'ams2'
    String size = '512mb'

    private String ip

    @TaskAction
    def setup() {
        // Creates the machine
        project.exec {
            executable 'docker-machine'
            args 'create',
                    '--driver=digitalocean',
                    "--digitalocean-access-token=${apiToken}",
                    "--digitalocean-image=docker",
                    "--digitalocean-region=${region}",
                    "--digitalocean-size=${size}",
                    dropletName
        }
        // Gets its IP
        ip = execute('docker-machine', 'ip', dropletName)
        // Displays the IP
        println "[${name}] Droplet ${dropletName} available at ${ip}"
    }

    String getIp() {
        return ip
    }
}