package net.nemerosa.ontrack.extension.environments.events

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.Event

interface EnvironmentsEventsFactory {

    fun pipelineCreation(pipeline: SlotPipeline): Event
    fun pipelineDeploying(pipeline: SlotPipeline): Event
    fun pipelineDeployed(pipeline: SlotPipeline): Event

}