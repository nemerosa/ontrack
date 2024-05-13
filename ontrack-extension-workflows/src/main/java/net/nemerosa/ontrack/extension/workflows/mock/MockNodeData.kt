package net.nemerosa.ontrack.extension.workflows.mock

import net.nemerosa.ontrack.model.annotations.APIDescription

data class MockNodeData(
    @APIDescription("Text associated with the node")
    val text: String,
    @APIDescription("Time to wait for the execution of the node")
    val waitMs: Long = 0,
    @APIDescription("Raising an error during the execution of the node")
    val error: Boolean = false,
)
