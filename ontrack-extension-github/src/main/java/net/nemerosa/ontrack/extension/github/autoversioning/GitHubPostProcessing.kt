package net.nemerosa.ontrack.extension.github.autoversioning

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.common.untilTimeout
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessing
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingInfo
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingMissingConfigException
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningTemplateRenderer
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.net.URLEncoder
import java.time.Duration
import java.util.*

@Component
class GitHubPostProcessing(
    extensionFeature: GitHubExtensionFeature,
    private val cachedSettingsService: CachedSettingsService,
    private val gitHubConfigurationService: GitHubConfigurationService,
    private val ontrackGitHubClientFactory: OntrackGitHubClientFactory,
) : AbstractExtension(extensionFeature), PostProcessing<GitHubPostProcessingConfig> {

    override val id: String = "github"

    override val name: String = "GitHub Actions workflow post processing"

    override fun parseAndValidate(config: JsonNode?): GitHubPostProcessingConfig =
        if (config != null && !config.isNull) {
            // Parsing
            config.parse()
        } else {
            throw PostProcessingMissingConfigException()
        }

    override fun postProcessing(
        config: GitHubPostProcessingConfig,
        autoVersioningOrder: AutoVersioningOrder,
        repositoryURI: String,
        repository: String,
        upgradeBranch: String,
        scm: SCM,
        avTemplateRenderer: AutoVersioningTemplateRenderer,
        onPostProcessingInfo: (info: PostProcessingInfo) -> Unit,
    ) {
        // Gets the settings
        val settings = cachedSettingsService.getCachedSettings(GitHubPostProcessingSettings::class.java)

        // Gets the name of the GitHub configuration
        val ghConfigName = config.config
            ?: settings.config?.takeIf { it.isNotBlank() }
            ?: throw GitHubPostProcessingConfigException("Default GitHub configuration is not defined in the settings.")

        // Loading the configuration
        val ghConfig = gitHubConfigurationService.findConfiguration(ghConfigName)
            ?: throw GitHubPostProcessingConfigException("Cannot find GitHub configuration with name: $ghConfigName")

        // Local repository
        if (!config.workflow.isNullOrBlank()) {
            runPostProcessing(
                ghConfig = ghConfig,
                repository = repository,
                workflow = config.workflow,
                branch = upgradeBranch,
                inputs = emptyMap(),
                settings = settings,
                onPostProcessingInfo = onPostProcessingInfo,
            )
        }
        // Common repository
        else {
            if (settings.repository.isNullOrBlank()) {
                throw GitHubPostProcessingConfigException("Default GitHub repository for auto versioning post processing is not defined.")
            }
            if (settings.workflow.isNullOrBlank()) {
                throw GitHubPostProcessingConfigException("Default GitHub workflow for auto versioning post processing is not defined.")
            }
            runPostProcessing(
                ghConfig = ghConfig,
                repository = settings.repository,
                workflow = settings.workflow,
                branch = settings.branch,
                inputs = mapOf(
                    "repository" to repository,
                    "upgrade_branch" to upgradeBranch,
                    "docker_image" to config.dockerImage,
                    "docker_command" to config.dockerCommand,
                    "commit_message" to config.commitMessage,
                    "version" to autoVersioningOrder.targetVersion,
                ),
                settings = settings,
                onPostProcessingInfo = onPostProcessingInfo,
            )
        }
    }

    private fun runPostProcessing(
        ghConfig: GitHubEngineConfiguration,
        repository: String,
        workflow: String,
        branch: String,
        inputs: Map<String, String>,
        settings: GitHubPostProcessingSettings,
        onPostProcessingInfo: (info: PostProcessingInfo) -> Unit,
    ) {
        // Getting the GH client
        val client = ontrackGitHubClientFactory.create(ghConfig).createGitHubRestTemplate()
        // Launches the workflow run
        val id = launchWorkflowRun(client, repository, workflow, branch, inputs)
        // Getting the workflow run
        val runId = findWorkflowRun(client, repository, workflow, branch, id, settings)
        // Sending back the URL of the workflow run
        val url = "${ghConfig.url}/${repository}/actions/runs/${runId}"
        onPostProcessingInfo(
            PostProcessingInfo(
                data = mapOf(
                    "url" to url
                )
            )
        )
        // Waiting until the workflow run completes
        waitUntilWorkflowRun(
            client = client,
            repository = repository,
            runId = runId,
            settings = settings,
        )
    }

    private fun waitUntilWorkflowRun(
        client: RestTemplate,
        repository: String,
        runId: Long,
        settings: GitHubPostProcessingSettings,
    ) {
        runBlocking {
            untilTimeout(
                name = "Waiting for workflow run $repository/$runId",
                retryCount = settings.retries,
                retryDelay = Duration.ofSeconds(settings.retriesDelaySeconds.toLong()),
            ) {
                val status = client.getForObject<WorkflowRun>("/repos/$repository/actions/runs/$runId").status
                if (status == "completed") {
                    true
                } else {
                    null // Not finished yet
                }
            }
        }
    }

    private fun findWorkflowRun(
        client: RestTemplate,
        repository: String,
        workflow: String,
        branch: String,
        id: String,
        settings: GitHubPostProcessingSettings,
    ): Long =
        runBlocking {
            untilTimeout(
                name = "Getting workflow run for $repository/$workflow/$branch",
                retryCount = settings.retries,
                retryDelay = Duration.ofSeconds(settings.retriesDelaySeconds.toLong()),
            ) {
                // Gets the list of runs
                val runs = getWorkflowRuns(client, repository, branch)
                // Checks the artifacts for each run
                runs.find { run ->
                    hasWorkflowId(client, repository, run.id, id)
                }?.id
            }
        }

    private fun hasWorkflowId(
        client: RestTemplate,
        repository: String,
        runId: Long,
        id: String,
    ): Boolean {
        val expectedName = "inputs-$id.properties"
        val artifacts = client.getForObject<JsonNode>("/repos/$repository/actions/runs/$runId/artifacts")
            .path("artifacts")
            .map {
                it.parse<Artifact>()
            }
        return artifacts.any {
            it.name == expectedName
        }
    }

    private fun getWorkflowRuns(
        client: RestTemplate,
        repository: String,
        branch: String,
    ): List<WorkflowRun> {
        val encodedBranch = URLEncoder.encode(branch, Charsets.UTF_8)
        return client.getForObject<JsonNode>("/repos/$repository/actions/runs?event=workflow_dispatch&branch=$encodedBranch")
            .path("workflow_runs")
            .map {
                it.parse()
            }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class Artifact(
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class WorkflowRun(
        val id: Long,
        @JsonProperty("head_branch")
        val headBranch: String,
        val status: String,
    )

    private fun launchWorkflowRun(
        client: RestTemplate,
        repository: String,
        workflow: String,
        branch: String,
        inputs: Map<String, String>,
    ): String {
        val id = UUID.randomUUID().toString()
        client.postForLocation(
            "/repos/$repository/actions/workflows/$workflow/dispatches",
            mapOf(
                "ref" to branch,
                "inputs" to (inputs + mapOf("id" to id)),
            ),
        )
        return id
    }

}