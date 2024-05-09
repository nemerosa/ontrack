package net.nemerosa.ontrack.extension.workflows.mock

data class MockNodeData(
    val text: String,
    val waitMs: Long = 0,
    val error: Boolean = false,
)
