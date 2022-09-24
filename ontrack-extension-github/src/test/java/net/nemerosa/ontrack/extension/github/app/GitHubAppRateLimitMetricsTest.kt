package net.nemerosa.ontrack.extension.github.app

import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.github.GitHubConfigurationProperties
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GitHubAppRateLimitMetricsTest {

    private lateinit var gitHubConfigurationProperties: GitHubConfigurationProperties
    private lateinit var gitHubConfigurationService: GitHubConfigurationService
    private lateinit var gitHubClientFactory: OntrackGitHubClientFactory
    private lateinit var meterRegistry: MeterRegistry
    private lateinit var metrics: GitHubAppRateLimitMetrics

    @BeforeEach
    fun init() {
        gitHubConfigurationProperties = GitHubConfigurationProperties()

        gitHubConfigurationService = mockk(relaxed = true)

        gitHubClientFactory = mockk()

        meterRegistry = mockk(relaxed = true)

        metrics = GitHubAppRateLimitMetrics(
            gitHubConfigurationProperties,
            gitHubConfigurationService,
            gitHubClientFactory,
            meterRegistry,
        )
    }

    @Test
    fun `Registration at startup by default`() {
        withConfiguration {
            metrics.start()
        }
        verify {
            gitHubConfigurationService.addConfigurationServiceListener(metrics)
        }
    }

    @Test
    fun `Registration at startup with explicit flag`() {
        gitHubConfigurationProperties.metrics.enabled = true
        withConfiguration {
            metrics.start()
        }
        verify {
            gitHubConfigurationService.addConfigurationServiceListener(metrics)
        }
    }

    @Test
    fun `No registration at startup`() {
        gitHubConfigurationProperties.metrics.enabled = false
        withConfiguration {
            metrics.start()
        }
        verify(exactly = 0) {
            gitHubConfigurationService.addConfigurationServiceListener(metrics)
        }
    }

    private fun withConfiguration(
        code: () -> Unit,
    ) {
        val configuration = GitHubEngineConfiguration(
            name = "test",
            url = null,
            oauth2Token = "xxx"
        )
        every {
            gitHubConfigurationService.configurations
        } returns listOf(
            configuration
        )
        code()
    }

}