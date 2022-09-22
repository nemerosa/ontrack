package net.nemerosa.ontrack.extension.github.app

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import net.nemerosa.ontrack.extension.github.GitHubConfigurationProperties
import net.nemerosa.ontrack.extension.github.client.GitHubRateLimit
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.model.support.ConfigurationServiceListener
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
) : MeterBinder, ConfigurationServiceListener<GitHubEngineConfiguration> {

    private var meterRegistry: MeterRegistry? = null

    override fun bindTo(registry: MeterRegistry) {
        if (gitHubConfigurationProperties.metrics.enabled) {
            gitHubConfigurationService.configurations.forEach {
                registerMetrics(registry, it)
            }
        }
    }

    override fun onNewConfiguration(configuration: GitHubEngineConfiguration) {
        meterRegistry?.let {
            registerMetrics(it, configuration)
        }
    }

    override fun onDeletedConfiguration(configuration: GitHubEngineConfiguration) {
        meterRegistry?.let {
            unregisterMetric(it, configuration, RATE_LIMIT_CORE_LIMIT_METRIC)
            unregisterMetric(it, configuration, RATE_LIMIT_CORE_REMAINING_METRIC)
            unregisterMetric(it, configuration, RATE_LIMIT_CORE_USED_METRIC)
            unregisterMetric(it, configuration, RATE_LIMIT_GRAPHQL_LIMIT_METRIC)
            unregisterMetric(it, configuration, RATE_LIMIT_GRAPHQL_REMAINING_METRIC)
            unregisterMetric(it, configuration, RATE_LIMIT_GRAPHQL_USED_METRIC)
        }
    }

    private fun registerMetrics(registry: MeterRegistry, configuration: GitHubEngineConfiguration) {
        val rateLimitFn = {
            val client = gitHubClientFactory.create(configuration)
            client.getRateLimit()
        }

        registerMetric(
            registry,
            configuration,
            rateLimitFn,
            RATE_LIMIT_CORE_LIMIT_METRIC
        ) { core.limit }

        registerMetric(
            registry,
            configuration,
            rateLimitFn,
            RATE_LIMIT_CORE_REMAINING_METRIC
        ) { core.remaining }

        registerMetric(
            registry,
            configuration,
            rateLimitFn,
            RATE_LIMIT_CORE_USED_METRIC
        ) { core.used }

        registerMetric(
            registry,
            configuration,
            rateLimitFn,
            RATE_LIMIT_GRAPHQL_LIMIT_METRIC
        ) { graphql.limit }

        registerMetric(
            registry,
            configuration,
            rateLimitFn,
            RATE_LIMIT_GRAPHQL_REMAINING_METRIC
        ) { graphql.remaining }

        registerMetric(
            registry,
            configuration,
            rateLimitFn,
            RATE_LIMIT_GRAPHQL_USED_METRIC
        ) { graphql.used }
    }

    private fun registerMetric(
        registry: MeterRegistry,
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
        }.tag("configuration", configuration.name).register(registry)
    }

    private fun unregisterMetric(registry: MeterRegistry, configuration: GitHubEngineConfiguration, metric: String) {
        registry.find(metric).tag("configuration", configuration.name).gauge()?.let {
            registry.remove(it)
        }
    }

    companion object {
        private const val RATE_LIMIT_METRIC = "ontrack_extension_github_ratelimit"
        private const val RATE_LIMIT_CORE_LIMIT_METRIC = "${RATE_LIMIT_METRIC}_core_limit"
        private const val RATE_LIMIT_CORE_REMAINING_METRIC = "${RATE_LIMIT_METRIC}_core_remaining"
        private const val RATE_LIMIT_CORE_USED_METRIC = "${RATE_LIMIT_METRIC}_core_used"
        private const val RATE_LIMIT_GRAPHQL_LIMIT_METRIC = "${RATE_LIMIT_METRIC}_graphql_limit"
        private const val RATE_LIMIT_GRAPHQL_REMAINING_METRIC = "${RATE_LIMIT_METRIC}_graphql_remaining_limit"
        private const val RATE_LIMIT_GRAPHQL_USED_METRIC = "${RATE_LIMIT_METRIC}_graphql_used"
    }
}