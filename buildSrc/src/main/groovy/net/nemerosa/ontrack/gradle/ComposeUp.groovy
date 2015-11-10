package net.nemerosa.ontrack.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Docker Compose `up` command
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
     * Rebuilding the containers?
     */
    boolean rebuild = true

    /**
     * Service to start
     */
    String service

    @TaskAction
    def run() {
        // Rebuilding first?
        if (rebuild) {
            rebuildImages()
        }
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

    protected def rebuildImages() {
        // Arguments
        List<?> args = ['build']
        // Service?
        if (service) {
            args << service
        }
        // Running
        compose(args as Object[])
    }

}
