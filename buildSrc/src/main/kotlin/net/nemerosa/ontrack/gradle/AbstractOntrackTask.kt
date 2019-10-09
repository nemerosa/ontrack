package net.nemerosa.ontrack.gradle

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnection
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input

abstract class AbstractOntrackTask : DefaultTask() {

    @Input
    var ontrackUrl: String = project.properties["ontrackUrl"] as? String? ?: "http://localhost:8080"

    @Input
    var ontrackUser: String = project.properties["ontrackUser"] as? String? ?: "admin"

    @Input
    var ontrackPassword: String = project.properties["ontrackPassword"] as? String? ?: "admin"

    protected fun getOntrackClient(logging: Boolean = true): Ontrack =
            OntrackConnection.create(ontrackUrl)
                    .authenticate(ontrackUser, ontrackPassword)
                    .run {
                        if (logging) {
                            logger {
                                logger.debug(it)
                            }
                        } else {
                            this
                        }
                    }.build()
}
