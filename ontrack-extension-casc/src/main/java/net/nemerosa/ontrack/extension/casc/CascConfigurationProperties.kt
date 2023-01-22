package net.nemerosa.ontrack.extension.casc

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Configuration properties for the configuration as code
 */
@Component
@ConfigurationProperties(prefix = "ontrack.config.casc")
class CascConfigurationProperties {

    /**
     * Is the configuration as code enabled?
     */
    var enabled = true

    /**
     * List of resources to load and to monitor for changes
     */
    var locations: List<String> = emptyList()

    /**
     * Reloading configuration
     */
    var reloading = CascReloadingConfigurationProperties()

    /**
     * Secrets configurations
     */
    var secrets = CascSecretConfigurationProperties()

    /**
     * Upload end point
     */
    var upload = CascUploadConfigurationProperties()

    /**
     * Reloading configuration
     */
    class CascReloadingConfigurationProperties {
        /**
         * Enables the creation of a job to reload the CasC.
         */
        var enabled = false

        /**
         * Cron schedule for the reloading. Leave blank or empty to disable the automated reloading.
         */
        var cron = ""
    }

    /**
     * Secrets configurations
     */
    class CascSecretConfigurationProperties {
        /**
         * Source for the secrets.
         *
         * Either "env" (default) or "file"
         */
        var type: String = "env"
        /**
         * Directory used to store the secret files (used only when type == "file"
         */
        var directory = ""
    }

    /**
     * Upload end point
     */
    class CascUploadConfigurationProperties {
        /**
         * Is the upload endpoint enabled?
         */
        var enabled = false
    }

}