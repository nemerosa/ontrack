package net.nemerosa.ontrack.extension.environments.trigger

import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus

data class SlotPipelineTriggerData(
    val pipelineId: String,
    val status: SlotPipelineStatus,
)
