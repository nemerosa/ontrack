package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker Compose `up` command
 *
 * -d                     Detached mode: Run containers in the background,
 print new container names.
 --no-color             Produce monochrome output.
 --no-deps              Don't start linked services.
 --force-recreate       Recreate containers even if their configuration and
 image haven't changed. Incompatible with --no-recreate.
 --no-recreate          If containers already exist, don't recreate them.
 Incompatible with --force-recreate.
 --no-build             Don't build an image, even if it's missing
 -t, --timeout TIMEOUT  Use this timeout in seconds for container shutdown
 when attached or when containers are already
 running. (default: 10)

 */
class ComposeUp extends AbstractCompose {

    /**
     * Detach mode
     */
    boolean detach = true

    /**
     * Recreate containers even if their configuration and image haven't changed.
     */
    boolean forceRecreate = false

    /**
     * Service to start
     */
    String service

    @TaskAction
    def run() {
        // Arguments
        List<?> args = []
        // Command
        args << 'up'
        // Detach mode
        if (detach) {
            args << '-d'
        }
        // Force recreate
        if (forceRecreate) {
            args << '--force-recreate'
        }
        // Service?
        if (service) {
            args << service
        }
        // Running
        compose(args as Object[])
    }

}
