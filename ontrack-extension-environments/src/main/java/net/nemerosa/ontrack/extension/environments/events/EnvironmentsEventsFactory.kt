package net.nemerosa.ontrack.extension.environments.events

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.Event

interface EnvironmentsEventsFactory {

    fun pipelineDeploying(pipeline: SlotPipeline): Event

}