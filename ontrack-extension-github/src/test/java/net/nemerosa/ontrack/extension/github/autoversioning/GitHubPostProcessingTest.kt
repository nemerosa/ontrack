package net.nemerosa.ontrack.extension.github.autoversioning

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningTemplateRenderer
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.junit.jupiter.api.Test

class GitHubPostProcessingTest {

    @Test
    fun `Templating of the GitHub parameters to the workflow`() {

        val client = mockk<OntrackGitHubClient>(relaxed = true)

        val ontrackGitHubClientFactory = mockk<OntrackGitHubClientFactory>()
        every { ontrackGitHubClientFactory.create(any()) } returns client

        val cachedSettingsService = mockk<CachedSettingsService>()

        val settings = GitHubPostProcessingSettings(
            config = "my-config",
            repository = "repository",
            workflow = "workflow.yml",
            branch = "main",
        )
        every { cachedSettingsService.getCachedSettings(GitHubPostProcessingSettings::class.java) } returns settings

        val gitHubConfigurationService = mockk<GitHubConfigurationService>()

        val gitHubConfig = mockk<GitHubEngineConfiguration>()
        every { gitHubConfigurationService.findConfiguration("my-config") } returns gitHubConfig

        every { gitHubConfig.url } returns "https://github.com"

        val processing = GitHubPostProcessing(
            extensionFeature = mockk(),
            cachedSettingsService = cachedSettingsService,
            gitHubConfigurationService = gitHubConfigurationService,
            ontrackGitHubClientFactory = ontrackGitHubClientFactory,
        )

        val config = GitHubPostProcessingConfig(
            dockerImage = "docker/image",
            dockerCommand = "command.sh ${'$'}${'$'}{VERSION}",
            commitMessage = "Commit message for version ${'$'}{VERSION}",
            config = null,
            workflow = null,
            parameters = listOf(
                GitHubPostProcessingConfigParam(
                    name = "param1",
                    value = "${'$'}{sourceBuild.release}"
                )
            )
        )

        val order = mockk<AutoVersioningOrder>()
        every { order.targetVersion } returns "1.0.0"

        val avTemplateRenderer = mockk<AutoVersioningTemplateRenderer>()
        every {
            avTemplateRenderer.render(
                "main",
                PlainEventRenderer.INSTANCE
            )
        } returns "main"
        every {
            avTemplateRenderer.render(
                "docker/image",
                PlainEventRenderer.INSTANCE
            )
        } returns "docker/image"
        every {
            avTemplateRenderer.render(
                "Commit message for version ${'$'}{VERSION}",
                PlainEventRenderer.INSTANCE
            )
        } returns "Commit message for version 1.0.0"
        every {
            avTemplateRenderer.render(
                "command.sh ${'$'}${'$'}{VERSION}",
                PlainEventRenderer.INSTANCE
            )
        } returns "command.sh ${'$'}{VERSION}"
        every {
            avTemplateRenderer.render(
                "${'$'}{sourceBuild.release}",
                PlainEventRenderer.INSTANCE
            )
        } returns "my-release"

        processing.postProcessing(
            config = config,
            autoVersioningOrder = order,
            repositoryURI = "uri://repository",
            repository = "repository",
            upgradeBranch = "av/upgrade",
            scm = mockk(),
            avTemplateRenderer = avTemplateRenderer,
        ) {}

        verify {
            client.launchWorkflowRun(
                repository = "repository",
                workflow = "workflow.yml",
                branch = "main",
                inputs = mapOf(
                    "repository" to "repository",
                    "upgrade_branch" to "av/upgrade",
                    "docker_image" to "docker/image",
                    "docker_command" to "command.sh ${'$'}{VERSION}",
                    "commit_message" to "Commit message for version 1.0.0",
                    "version" to "1.0.0",
                    "param1" to "my-release",
                ),
                retries = 10,
                retriesDelaySeconds = 30,
            )
        }

    }

}