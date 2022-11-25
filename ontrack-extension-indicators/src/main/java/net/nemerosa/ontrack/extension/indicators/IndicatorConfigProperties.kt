package net.nemerosa.ontrack.extension.indicators

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * Static configuration for the management of indicators
 */
@Component
@ConfigurationProperties(prefix = "ontrack.extension.indicators")
@Validated
class IndicatorConfigProperties {

    /**
     * Import configuration
     */
    var importing = ImportConfig()

    /**
     * Metrics configuration
     */
    var metrics = MetricsConfig()

    /**
     * Import configuration
     */
    class ImportConfig {
        /**
         * When a category/type does not exist any longer for a given import ID, must it be deleted?
         */
        var deleting: Boolean = false
    }

    /**
     * Metrics configuration
     */
    class MetricsConfig {
        /**
         * Enabling the scheduled export of metrics (a manual job is always available)
         */
        var enabled: Boolean = true

        /**
         * Schedule
         */
        var cron: String = "@daily"
    }

}
