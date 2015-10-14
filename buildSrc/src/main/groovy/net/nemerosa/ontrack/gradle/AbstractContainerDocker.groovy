package net.nemerosa.ontrack.gradle

import org.gradle.api.GradleException

/**
 * Docker stop task
 */
abstract class AbstractContainerDocker extends AbstractDocker {

    String startTask
    String containerName

    protected String getContainer() {
        if (startTask) {
            // Gets the start task
            def task = project.tasks.getByName(startTask) as DockerStart
            // Stopping the container
            println "[${name}] Stopping container at ${task.cid} created by ${startTask}"
            // OK
            return task.cid
        } else if (containerName) {
            println "[${name}] Stopping container ${containerName}"
            return containerName
        } else {
            throw new GradleException("Either `startTask` or `containerName` must be specified.")
        }
    }

}
