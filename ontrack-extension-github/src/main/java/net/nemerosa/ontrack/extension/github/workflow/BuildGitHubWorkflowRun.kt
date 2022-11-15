package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Link between a Build and a GitHub Workflow run.
 *
 * @property runId ID of the run
 * @property url Link to the GitHub Workflow run
 * @property name Name of the workflow
 * @property runNumber Number of the run
 * @property running True if the run is still flagged as running
 * @property event Event having led to the creation of this build
 */
data class BuildGitHubWorkflowRun(
    val runId: Long,
    val url: String,
    val name: String,
    val runNumber: Int,
    val running: Boolean,
    @APIDescription("Event having led to the creation of this build")
    val event: String?,
) {
    companion object {
        /**
         * Replacing a run by another, based on its [runId], or adds it to the end of the list
         *
         * @param runs List of runs to edit
         * @param run New workflow run
         * @return `true` if the list was changed
         */
        fun edit(runs: MutableList<BuildGitHubWorkflowRun>, run: BuildGitHubWorkflowRun): Boolean {
            val index = runs.indexOfFirst { it.runId == run.runId }
            return if (index < 0) {
                runs += run
                true // List changed
            } else {
                val existing = runs[index]
                if (existing != run) {
                    runs[index] = run
                    true // List changed
                } else {
                    false // List unchanged
                }
            }
        }
    }
}