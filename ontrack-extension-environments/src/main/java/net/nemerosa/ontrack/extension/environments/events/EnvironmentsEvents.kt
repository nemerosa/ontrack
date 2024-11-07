package net.nemerosa.ontrack.extension.environments.events

import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.events.SimpleEventType
import net.nemerosa.ontrack.model.events.eventContext
import net.nemerosa.ontrack.model.events.eventValue

object EnvironmentsEvents {

    const val EVENT_PIPELINE_ID = "PIPELINE_ID"

    val PIPELINE_CREATION: EventType = SimpleEventType(
        id = "slot-pipeline-creation",
        template = """
            Pipeline ${'$'}{pipeline} has started.
        """.trimIndent(),
        description = "When a slot pipeline has started",
        context = eventContext(
            eventValue(EVENT_PIPELINE_ID, "ID of the pipeline"),
        )
    )

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

    val PIPELINE_DEPLOYED: EventType = SimpleEventType(
        id = "slot-pipeline-deployed",
        template = """
            Pipeline ${'$'}{pipeline} has been deployed.
        """.trimIndent(),
        description = "When a slot pipeline has completed its deployment",
        context = eventContext(
            eventValue(EVENT_PIPELINE_ID, "ID of the pipeline"),
        )
    )

}