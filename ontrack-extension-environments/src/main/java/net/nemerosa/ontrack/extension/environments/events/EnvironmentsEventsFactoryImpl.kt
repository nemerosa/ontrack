package net.nemerosa.ontrack.extension.environments.events

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
class EnvironmentsEventsFactoryImpl : EnvironmentsEventsFactory {

    override fun pipelineDeploying(pipeline: SlotPipeline): Event =
        Event.of(EnvironmentsEvents.PIPELINE_DEPLOYING)
            .with(EnvironmentsEvents.EVENT_PIPELINE_ID, pipeline.id)
            .build()

}