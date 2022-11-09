package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Property which links a GitHub Workflow job to an Ontrack validation run.
 *
 * @property runId ID of the run
 * @property url Link to the GitHub Workflow job
 * @property name Name of the workflow
 * @property runNumber Number of the run
 * @property job Name of the job
 * @property running True if the job is still flagged as running
 * @property event Event having led to the creation of this validation
 */
class ValidationRunGitHubWorkflowJobProperty(
    val runId: Long,
    val url: String,
    val name: String,
    val runNumber: Int,
    val job: String,
    val running: Boolean,
    @APIDescription("Event having led to the creation of this validation")
    val event: String?,
)
