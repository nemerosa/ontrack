package net.nemerosa.ontrack.extension.environments

data class SlotPipelineStub(
    val environmentId: String,
    val environmentName: String,
    val slotId: String,
    val qualifier: String,
    val pipelineId: String,
)
