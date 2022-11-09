package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Property which links a GitHub Workflow run to an Ontrack build.
 *
 * @property runId ID of the run
 * @property url Link to the GitHub Workflow run
 * @property name Name of the workflow
 * @property runNumber Number of the run
 * @property running True if the run is still flagged as running
 * @property event Event having led to the creation of this build
 */
class BuildGitHubWorkflowRunProperty(
    val runId: Long,
    val url: String,
    val name: String,
    val runNumber: Int,
    val running: Boolean,
    @APIDescription("Event having led to the creation of this build")
    val event: String?,
)