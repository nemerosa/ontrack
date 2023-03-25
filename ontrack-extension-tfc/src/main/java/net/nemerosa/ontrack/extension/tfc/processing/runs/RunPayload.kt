package net.nemerosa.ontrack.extension.tfc.processing.runs

data class RunPayload(
    val runUrl: String,
    val runId: String,
    val workspaceId: String,
    val workspaceName: String,
    val organizationName: String,
    val message: String,
    val trigger: String,
    val runStatus: String,
)