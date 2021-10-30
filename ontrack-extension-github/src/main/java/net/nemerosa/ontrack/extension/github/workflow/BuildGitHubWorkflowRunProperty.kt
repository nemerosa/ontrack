package net.nemerosa.ontrack.extension.github.workflow

/**
 * Property which links a GitHub Workflow run to an Ontrack build.
 *
 * @property runId ID of the run
 * @property url Link to the GitHub Workflow run
 * @property name Name of the workflow
 * @property runNumber Number of the run
 * @property running True if the run is still flagged as running
 */
class BuildGitHubWorkflowRunProperty(
    val runId: Long,
    val url: String,
    val name: String,
    val runNumber: Int,
    val running: Boolean,
)