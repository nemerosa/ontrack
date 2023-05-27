package net.nemerosa.ontrack.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input

import net.nemerosa.ontrack.gradle.client.OntrackClient

abstract class AbstractOntrackTask : DefaultTask() {

    @Input
    var ontrackUrl: String = project.properties["ontrackUrl"] as? String? ?: "http://localhost:8080"

    @Input
    var ontrackToken: String? = project.properties["ontrackToken"] as? String?

    protected fun getOntrackClient(logging: Boolean = true): OntrackClient =
            OntrackClient(
                    url = ontrackUrl,
                    token = ontrackToken
                            ?: error("Ontrack connection is required. Please set the `ontrackToken` Gradle property."),
                    logger = if (logging) {
                        {
                            logger.info(it)
                        }
                    } else {
                        {
                            // NOP
                        }
                    }
            )
}
