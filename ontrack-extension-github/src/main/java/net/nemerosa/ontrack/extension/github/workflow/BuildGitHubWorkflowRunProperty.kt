package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationList

/**
 * Links between GitHub Workflow runs and an Ontrack build.
 *
 * @property workflows All workflows associated to a build.
 */
data class BuildGitHubWorkflowRunProperty(
    @APIDescription("All workflows associated to a build.")
    @DocumentationList
    val workflows: List<BuildGitHubWorkflowRun>,
) {
    /**
     * Gets a workflow run using its [run id][BuildGitHubWorkflowRun.runId].
     */
    fun findRun(runId: Long): BuildGitHubWorkflowRun? = workflows.find { it.runId == runId }
}