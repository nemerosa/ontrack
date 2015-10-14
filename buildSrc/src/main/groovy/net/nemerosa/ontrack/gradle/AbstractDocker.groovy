package net.nemerosa.ontrack.gradle

import org.gradle.api.GradleException

import java.util.regex.Pattern

/**
 * Docker base class
 */
abstract class AbstractDocker extends AbstractCDTask {

    String machine

    /**
     * Docker command
     */
    String docker(Object... arguments) {
        List<?> list = getDockerConfig()
        list.addAll(arguments as List)
        execute('docker', list)
    }

    /**
     * Utility method to get a published port
     */
    int getPublishedPort (String cid, int port) {
        def output = docker('port', cid, port)
        def m = Pattern.compile(/^.*:(\d+)$/).matcher(output)
        if (m.matches()) {
            m.group(1) as int
        } else {
            throw new GradleException("Cannot parse port from ${output}")
        }
    }

    /**
     * Gets the initial arguments for a Docker command
     */
    List<String> getDockerConfig() {
        if (machine) {
            println "[${name}] Using Docker Machine ${machine}"
            def config = execute('docker-machine', 'config', machine)
            return config.split(' ') as List
        } else {
            return []
        }
    }

}