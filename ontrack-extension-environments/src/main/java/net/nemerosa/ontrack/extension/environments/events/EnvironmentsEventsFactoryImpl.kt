package net.nemerosa.ontrack.extension.environments.events

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.stereotype.Component

@Component
class EnvironmentsEventsFactoryImpl(
    private val eventFactory: EventFactory,
) : EnvironmentsEventsFactory, StartupService {

    override fun pipelineCreation(pipeline: SlotPipeline): Event =
        Event.of(EnvironmentsEvents.PIPELINE_CREATION)
            .with(EnvironmentsEvents.EVENT_PIPELINE_ID, pipeline.id)
            .build()

    override fun pipelineDeploying(pipeline: SlotPipeline): Event =
        Event.of(EnvironmentsEvents.PIPELINE_DEPLOYING)
            .with(EnvironmentsEvents.EVENT_PIPELINE_ID, pipeline.id)
            .build()

    override fun pipelineDeployed(pipeline: SlotPipeline): Event =
        Event.of(EnvironmentsEvents.PIPELINE_DEPLOYED)
            .with(EnvironmentsEvents.EVENT_PIPELINE_ID, pipeline.id)
            .build()

    override fun getName(): String = "Registration of environment events"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        eventFactory.register(EnvironmentsEvents.PIPELINE_CREATION)
        eventFactory.register(EnvironmentsEvents.PIPELINE_DEPLOYING)
        eventFactory.register(EnvironmentsEvents.PIPELINE_DEPLOYED)
    }

}