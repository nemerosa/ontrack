package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker start class
 */
class DockerStart extends AbstractDocker {

    String image

    Map<String, String> environment = [:]

    Map<String, String> volumes = [:]

    boolean urandom = true

    Map<Object, Integer> ports = [:]

    String containerName

    String command

    boolean tty = false

    boolean restart = false

    private String cid

    private Map<Integer, Integer> actualPorts

    @TaskAction
    def start() {
        // Checks required arguments
        check(getImage(), "`image` property is required")
        // Arguments
        List<String> arguments = []
        // Logging
        println "[${name}] Starting ${getImage()}"
        // Main arguments
        arguments.add 'run'
        arguments.add '--detach'
        // TTY?
        if (isTty()) {
            println "[${name}] Enabling TTY"
            arguments.add '--tty'
        }
        // Environment
        getEnvironment().each { name, value ->
            println "[${name}] Environment ${name} = ${value}"
            arguments.add "--env=${name}=${value}" as String
        }
        // Volume
        getVolumes().each { host, container ->
            println "[${name}] Volume ${host} mapped on ${container}"
            arguments.add "--volume=${host}:${container}"
        }
        // Urandom mapping?
        if (urandom) {
            println "[${name}] Mapping /dev/urandom host source to /dev/random"
            arguments.add "--volume=/dev/urandom:/dev/random"
        }
        // Port publication
        getPorts().each { container, host ->
            if (host <= 0) {
                println "[${name}] Port ${container} mapped on random port"
                arguments.add "--publish=${container}"
            } else {
                println "[${name}] Port ${container} mapped on ${host} host port"
                arguments.add "--publish=${container}:${host}"
            }
        }
        // Restart policy
        if (isRestart()) {
            println "[${name}] Restarting on host restart"
            arguments.add '--restart=always'
        }
        // Container name
        def actualContainerName = getContainerName()
        if (actualContainerName) {
            println "[${name}] Using container name ${actualContainerName}"
            arguments.add "--name=${actualContainerName}"
        }
        // Image to start
        arguments.add getImage()
        // Command
        String actualCommand = getCommand()
        if (actualCommand) {
            println "[${name}] Using command ${actualCommand}"
            arguments.add actualCommand
        }
        // Starting the container
        cid = docker(arguments as String[])
        println "[${name}] Container ${cid} started"
        // Getting the published ports
        this.actualPorts = ports.collectEntries { Object container, host ->
            int actualPort = getPublishedPort(this.cid, container as int)
            println "[${name}] Port ${container} mapped on actual ${actualPort} host port"
            return [container as int, actualPort]
        }
    }

    protected static def check(def value, String message) {
        if (!value) {
            throw new Exception(message)
        }
    }

    String getCid() {
        cid
    }

    Map<Integer, Integer> getActualPorts() {
        return actualPorts
    }

    int getActualPort(int container) {
        return actualPorts[container]
    }
}