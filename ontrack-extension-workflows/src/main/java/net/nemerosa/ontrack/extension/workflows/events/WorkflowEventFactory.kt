package net.nemerosa.ontrack.extension.workflows.events

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.stereotype.Component

@Component
class WorkflowEventFactory(
    private val eventFactory: EventFactory,
) : StartupService {

    fun workflowStandalone(): Event = Event.of(WorkflowEvents.WORKFLOW_STANDALONE)
        .build()

    override fun getName(): String = "Registration of workflow events"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        eventFactory.register(WorkflowEvents.WORKFLOW_STANDALONE)
    }

}