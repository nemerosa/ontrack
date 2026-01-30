package net.nemerosa.ontrack.extension.github.app

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.github.GitHubConfigurationProperties
import net.nemerosa.ontrack.extension.github.client.GitHubRateLimit
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.model.support.ConfigurationServiceListener
import net.nemerosa.ontrack.model.support.StartupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Exporting rate limits of the GitHub App configurations as metrics.
 */
@Component
@Transactional(readOnly = true)
class GitHubAppRateLimitMetrics(
    private val gitHubConfigurationProperties: GitHubConfigurationProperties,
    private val gitHubConfigurationService: GitHubConfigurationService,
    private val gitHubClientFactory: OntrackGitHubClientFactory,
    private val meterRegistry: MeterRegistry,
) : ConfigurationServiceListener<GitHubEngineConfiguration>, StartupService {

    private val logger: Logger = LoggerFactory.getLogger(GitHubAppRateLimitMetrics::class.java)

    override fun getName(): String = "GitHub App Rate Limit metrics"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        if (gitHubConfigurationProperties.metrics.enabled) {
            gitHubConfigurationService.addConfigurationServiceListener(this)
            gitHubConfigurationService.configurations.forEach {
                registerMetrics(it)
            }
        }
    }

    override fun onNewConfiguration(configuration: GitHubEngineConfiguration) {
        registerMetrics(configuration)
    }

    override fun onDeletedConfiguration(configuration: GitHubEngineConfiguration) {
        logger.info("Unregistering metrics for configuration ${configuration.name}")
        unregisterMetric(configuration, GitHubAppRateLimitMetricsNames.Core.ontrack_extension_github_ratelimit_core_limit)
        unregisterMetric(configuration, GitHubAppRateLimitMetricsNames.Core.ontrack_extension_github_ratelimit_core_remaining)
        unregisterMetric(configuration, GitHubAppRateLimitMetricsNames.Core.ontrack_extension_github_ratelimit_core_used)
        unregisterMetric(configuration, GitHubAppRateLimitMetricsNames.GraphQL.ontrack_extension_github_ratelimit_graphql_limit)
        unregisterMetric(configuration, GitHubAppRateLimitMetricsNames.GraphQL.ontrack_extension_github_ratelimit_graphql_remaining)
        unregisterMetric(configuration, GitHubAppRateLimitMetricsNames.GraphQL.ontrack_extension_github_ratelimit_graphql_used)
    }

    private fun registerMetrics(configuration: GitHubEngineConfiguration) {

        logger.info("Registering metrics for configuration ${configuration.name}")

        val rateLimitFn = {
            val client = gitHubClientFactory.create(configuration)
            client.getRateLimit()
        }

        registerMetric(
            configuration,
            rateLimitFn,
            GitHubAppRateLimitMetricsNames.Core.ontrack_extension_github_ratelimit_core_limit
        ) { core.limit }

        registerMetric(
            configuration,
            rateLimitFn,
            GitHubAppRateLimitMetricsNames.Core.ontrack_extension_github_ratelimit_core_remaining
        ) { core.remaining }

        registerMetric(
            configuration,
            rateLimitFn,
            GitHubAppRateLimitMetricsNames.Core.ontrack_extension_github_ratelimit_core_used
        ) { core.used }

        registerMetric(
            configuration,
            rateLimitFn,
            GitHubAppRateLimitMetricsNames.GraphQL.ontrack_extension_github_ratelimit_graphql_limit
        ) { graphql.limit }

        registerMetric(
            configuration,
            rateLimitFn,
            GitHubAppRateLimitMetricsNames.GraphQL.ontrack_extension_github_ratelimit_graphql_remaining
        ) { graphql.remaining }

        registerMetric(
            configuration,
            rateLimitFn,
            GitHubAppRateLimitMetricsNames.GraphQL.ontrack_extension_github_ratelimit_graphql_used
        ) { graphql.used }
    }

    private fun registerMetric(
        configuration: GitHubEngineConfiguration,
        rateLimitFn: () -> GitHubRateLimit?,
        metric: String,
        valueFn: GitHubRateLimit.() -> Int,
    ) {
        Gauge.builder(
            metric,
            rateLimitFn
        ) {
            rateLimitFn()?.valueFn()?.toDouble() ?: 0.0
        }.tag("configuration", configuration.name).register(meterRegistry)
    }

    private fun unregisterMetric(configuration: GitHubEngineConfiguration, metric: String) {
        meterRegistry.find(metric).tag("configuration", configuration.name).gauges().forEach {
            meterRegistry.remove(it)
        }
    }

}