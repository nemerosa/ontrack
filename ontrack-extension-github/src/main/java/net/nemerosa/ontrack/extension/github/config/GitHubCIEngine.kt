package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRun
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitHubCIEngine(
    private val propertyService: PropertyService,
) : CIEngine {

    override val name: String = "github"

    override fun matchesEnv(env: Map<String, String>): Boolean = env[GITHUB_ACTIONS] == "true"

    override fun getScmUrl(env: Map<String, String>): String? {
        val serverUrl = env[GITHUB_SERVER_URL]
        val repository = env[GITHUB_REPOSITORY]
        return if (serverUrl != null && repository != null) {
            "$serverUrl/$repository.git"
        } else {
            null
        }
    }

    override fun getProjectName(env: Map<String, String>): String? =
        super.getProjectName(env) ?: detectProjectName(env)

    override fun getBranchName(env: Map<String, String>): String? = super.getBranchName(env) ?: env[GITHUB_REF_NAME]

    override fun getBuildSuffix(env: Map<String, String>): String? =
        super.getBuildSuffix(env) ?: env[GITHUB_RUN_NUMBER]

    private fun detectProjectName(env: Map<String, String>): String? {
        val fullName = env[GITHUB_REPOSITORY] ?: return null
        return fullName.substringAfter("/")
    }

    override fun configureBuild(
        build: Build,
        configuration: BuildConfiguration,
        env: Map<String, String>
    ) {
        val serverUrl = env[GITHUB_SERVER_URL] ?: return
        val repository = env[GITHUB_REPOSITORY] ?: return
        val runId = env[GITHUB_RUN_ID]?.toLongOrNull() ?: return
        val url = "$serverUrl/$repository/actions/runs/$runId"
        val name = env[GITHUB_WORKFLOW] ?: return
        val runNumber = env[GITHUB_RUN_NUMBER]?.toIntOrNull() ?: return
        val event = env[GITHUB_EVENT_NAME] ?: return
        propertyService.editProperty(
            entity = build,
            propertyType = BuildGitHubWorkflowRunPropertyType::class.java,
            data = BuildGitHubWorkflowRunProperty(
                workflows = listOf(
                    BuildGitHubWorkflowRun(
                        runId = runId,
                        url = url,
                        name = name,
                        runNumber = runNumber,
                        running = false,
                        event = event,
                    )
                ),
            )
        )
    }

    companion object {
        const val GITHUB_SERVER_URL = "GITHUB_SERVER_URL"
        const val GITHUB_REPOSITORY = "GITHUB_REPOSITORY"
        const val GITHUB_REF_NAME = "GITHUB_REF_NAME"
        const val GITHUB_RUN_ID = "GITHUB_RUN_ID"
        const val GITHUB_RUN_NUMBER = "GITHUB_RUN_NUMBER"
        const val GITHUB_WORKFLOW = "GITHUB_WORKFLOW"
        const val GITHUB_EVENT_NAME = "GITHUB_EVENT_NAME"
        const val GITHUB_ACTIONS = "GITHUB_ACTIONS"
    }
}