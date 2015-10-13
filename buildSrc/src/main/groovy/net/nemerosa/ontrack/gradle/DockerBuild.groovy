package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker start class
 */
class DockerBuild extends AbstractDocker {

    String tag = 'nemerosa/ontrack'

    File dir

    @TaskAction
    def start() {
        // Arguments
        List<String> arguments = getDockerConfig()
        // Logging
        println "[${name}] Building ${tag} from ${dir}..."
        // All arguments
        arguments.addAll([
                'build',
                '--tag',
                tag,
                dir.absolutePath
        ])
        // Runs the build
        execute('docker', arguments)
    }

}