package net.nemerosa.ontrack.extension.github.ingestion

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Static configuration for the ingestion.
 *
 * @property processing Queue configuration
 */
@Component
@ConfigurationProperties(prefix = IngestionConfigProperties.PREFIX)
class IngestionConfigProperties(
    var hook: HookConfig = HookConfig(),
    var processing: ProcessingConfig = ProcessingConfig(),
) {
    /**
     * Hook configuration
     *
     * @property
     */
    class HookConfig(
        var signature: HookSignatureConfig = HookSignatureConfig(),
    )

    /**
     * Hook signature configuration
     *
     * @property disabled Set to `true` to disable the signature checks (OK for testing, NOT for production)
     */
    class HookSignatureConfig(
        var disabled: Boolean = false,
    )

    /**
     * Processing configuration
     *
     * @property async Behaviour of the processing. By default, true, using a RabbitMQ engine
     * @property repositories List of specific bindings
     */
    class ProcessingConfig(
        var async: Boolean = true,
        var repositories: Map<String, RepositoryQueueConfig> = emptyMap(),
    )

    /**
     * Queue configuration for a repository and/or organization
     *
     * @property owner Regex for the repository owner, null for match all
     * @property repository Regex for the repository name, null for match all
     */
    class RepositoryQueueConfig(
        var owner: String? = null,
        var repository: String? = null,
    ) {
        fun matches(owner: String, repository: String) =
            matching(this.owner?.toRegex(), owner) && matching(this.repository?.toRegex(), repository)

        private fun matching(regex: Regex?, value: String): Boolean =
            regex == null || regex.matches(value)
    }

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.github.ingestion"
    }
}