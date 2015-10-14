package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker start class
 */
class DockerOntrackStart extends DockerStart {

    File data

    File conf

    String profile = 'prod'

    boolean exposePort = false

    DockerOntrackStart() {
        this.image = 'nemerosa/ontrack:latest'
    }

    @Override
    Map<String, String> getEnvironment() {
        return super.getEnvironment() + [
                PROFILE: profile
        ]
    }

    @Override
    Map<String, String> getVolumes() {
        def map = super.getVolumes()
        if (data) {
            map.put(data.absolutePath, '/var/ontrack/data')
        }
        if (data) {
            map.put(conf.absolutePath, '/var/ontrack/conf')
        }
        return map
    }

    @Override
    Map<Integer, Integer> getPorts() {
        def map = super.getPorts()
        if (exposePort) {
            map.put(443, 443)
        } else {
            map.put(443, 0)
        }
        return map
    }

    @TaskAction
    def start() {
        super.start()
        project.ext.acceptanceOntrackPort = getActualPort(443)
    }
}