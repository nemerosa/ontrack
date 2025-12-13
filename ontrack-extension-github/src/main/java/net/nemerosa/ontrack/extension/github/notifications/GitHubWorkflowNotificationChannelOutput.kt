package net.nemerosa.ontrack.extension.github.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.docs.DocumentationList

data class GitHubWorkflowNotificationChannelOutput(
    @APIDescription("URL of the GitHub instance")
    @APILabel("GitHub URL")
    val url: String,
    @APIDescription("Repository owner for the workflow")
    @APILabel("Owner")
    val owner: String,
    @APIDescription("Repository name for the workflow")
    @APILabel("Repository")
    val repository: String,
    @APIDescription("Workflow ID (like main.yml)")
    @APILabel("Workflow ID")
    val workflowId: String,
    @APIDescription("Git reference (branch, tag or commit SHA) for the workflow")
    @APILabel("Reference")
    val reference: String,
    @APIDescription("Inputs to send to to the workflow dispatch")
    @APILabel("Inputs")
    @DocumentationList
    val inputs: List<GitHubWorkflowNotificationChannelConfigInput> = emptyList(),
    @APIDescription("Workflow run ID, will be filled in only if the workflow is actually launched")
    @APILabel("Workflow run ID")
    val workflowRunId: Long? = null,
) {
    fun withWorkflowRunId(workflowRunId: Long) = copy(workflowRunId = workflowRunId)
}
