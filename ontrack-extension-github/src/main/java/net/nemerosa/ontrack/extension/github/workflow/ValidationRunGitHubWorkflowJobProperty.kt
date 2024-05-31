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
    @APIDescription("ID of the run")
    val runId: Long,
    @APIDescription("Link to the GitHub Workflow run")
    val url: String,
    @APIDescription("Name of the workflow")
    val name: String,
    @APIDescription("Number of the run")
    val runNumber: Int,
    @APIDescription("True if the run is still flagged as running")
    val running: Boolean,
    @APIDescription("Event having led to the creation of this validation")
    val event: String?,
    @APIDescription("Name of the workflow job which created this validation")
    val job: String,
)
