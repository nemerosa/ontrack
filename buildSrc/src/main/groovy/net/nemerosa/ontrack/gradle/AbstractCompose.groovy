package net.nemerosa.ontrack.gradle

import org.gradle.api.GradleException

import java.util.regex.Pattern

/**
 * Docker base class
 */
abstract class AbstractCompose extends AbstractCDTask {

    static final Pattern bashExportPattern = Pattern.compile('export (.*)="(.*)"')

    /**
     * Name of the Docker machine to use. If not defined, the current `docker` configuration
     * will be used.
     */
    String machine

    /**
     * Host reference when not using the machine's IP
     */
    String host

    /**
     * Alternative project compose file(s)
     */
    List<String> projectFiles

    /**
     * Project name
     */
    String projectName

    /**
     * Verbose flag
     */
    boolean verbose

    /**
     * Gets the machine host
     */
    String getHost() {
        if (host) {
            return host
        } else if (machine) {
            return execute('docker-machine', 'ip', machine)
        } else {
            return 'localhost'
        }
    }

    /**
     * Gets the published port for a service
     */
    int getPublishedPort(String service, int containerPort) {
        def output = compose('port', service, containerPort)
        def m = Pattern.compile(/^.*:(\d+)$/).matcher(output)
        if (m.matches()) {
            m.group(1) as int
        } else {
            throw new GradleException("Cannot parse port from ${output}")
        }
    }

    /**
     * Compose command
     */
    String compose(Object... arguments) {
        List<String> list = []
        // General options
        if (projectFiles) {
            projectFiles.each { projectFile ->
                list.add("--file")
                list.add(projectFile)
            }
        }
        if (projectName) {
            list.add("--project-name")
            list.add(projectName)
        }
        if (verbose) {
            list.add("--verbose")
        }
        // Adds all arguments
        list.addAll(arguments.collect { it as String } as List)
        // Running
        execute(dockerEnvironment, 'docker-compose', list)
    }

    /**
     * Gets the Docker environment variables
     */
    Map getDockerEnvironment() {
        if (machine) {
            println "[${name}] Using Docker Machine ${machine}"
            // Bash output
            String bash = execute('docker-machine', 'env', '--shell', 'bash', machine)
            // Parsing
            return bash.readLines()
                .collect { bashExportPattern.matcher(it) }
                .findAll { it.matches() }
                .collectEntries { [ it.group(1), it.group(2) ] }
        } else {
            return [:]
        }
    }

}