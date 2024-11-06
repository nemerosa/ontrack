package net.nemerosa.ontrack.extension.environments.events

import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.events.SimpleEventType
import net.nemerosa.ontrack.model.events.eventContext
import net.nemerosa.ontrack.model.events.eventValue

object EnvironmentsEvents {

    const val EVENT_PIPELINE_ID = "PIPELINE_ID"

    val PIPELINE_DEPLOYING: EventType = SimpleEventType(
        id = "slot-pipeline-deploying",
        template = """
            Pipeline ${'$'}{pipeline} is starting its deployment.
        """.trimIndent(),
        description = "When a slot pipeline is starting its deployment",
        context = eventContext(
            eventValue(EVENT_PIPELINE_ID, "ID of the pipeline"),
        )
    )

}