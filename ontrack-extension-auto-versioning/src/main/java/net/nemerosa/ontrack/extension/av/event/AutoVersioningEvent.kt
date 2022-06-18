package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.events.SimpleEventType
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.stereotype.Component

@Component
class AutoVersioningEvent(
    private val eventFactory: EventFactory,
) : StartupService {

    override fun getName(): String = "Registration of auto versioning events"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        eventFactory.register(AUTO_VERSIONING_SUCCESS)
        eventFactory.register(AUTO_VERSIONING_ERROR)
    }

    companion object {

        val AUTO_VERSIONING_SUCCESS: EventType = SimpleEventType.of(
            "auto-versioning-success",
            "Auto versioning of ${this@branch.project.name}/${this@branch.name} for dependency ${dependency.project.name}/${dependency.name} version \"2.0.0\" has failed."
        )

        val AUTO_VERSIONING_ERROR: EventType = SimpleEventType.of(
            "auto-versioning-error",
            "TODO"
        )

    }

}