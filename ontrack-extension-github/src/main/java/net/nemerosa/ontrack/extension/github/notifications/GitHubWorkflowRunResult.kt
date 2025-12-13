package net.nemerosa.ontrack.extension.github.notifications

data class GitHubWorkflowRunResult(
    val error: String? = null,
    val runId: Long? = null,
)
