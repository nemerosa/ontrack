package net.nemerosa.ontrack.extension.tfc.service

data class RunPayload(
    val parameters: TFCParameters,
    val runUrl: String,
    val runId: String,
    val workspaceId: String,
    val workspaceName: String,
    val organizationName: String,
    val message: String,
    val trigger: String,
    val runStatus: String,
)