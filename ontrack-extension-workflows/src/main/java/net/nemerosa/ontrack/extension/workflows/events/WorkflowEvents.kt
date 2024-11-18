package net.nemerosa.ontrack.extension.workflows.events

import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.events.SimpleEventType
import net.nemerosa.ontrack.model.events.eventContext

object WorkflowEvents {

    val WORKFLOW_STANDALONE: EventType = SimpleEventType(
        id = "worflow_standalone",
        template = "Started a standalone workflow",
        description = "Event created when launching a standalone workflow",
        context = eventContext(/* No attribute */)
    )

}