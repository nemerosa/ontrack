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
    boolean removeFirst = false
    boolean backups = false

    private String ip

    @TaskAction
    def setup() {
        // Removing any previous machine
        if (removeFirst) {
            println "[${name}] Removing machine ${dropletName}..."
            project.exec {
                executable 'docker-machine'
                ignoreExitValue true
                args 'rm',
                        '--force',
                        dropletName
            }
        }
        // Creates the machine
        println "[${name}] Creating ${size} machine ${dropletName} in ${region}..."
        project.exec {
            executable 'docker-machine'
            args 'create',
                    '--driver=digitalocean',
                    "--digitalocean-access-token=${apiToken}",
                    "--digitalocean-image=docker",
                    "--digitalocean-region=${region}",
                    "--digitalocean-size=${size}",
                    "--digitalocean-backups=${backups}",
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