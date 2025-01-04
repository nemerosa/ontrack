package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Configuration properties for the configuration as code
 */
@Component
@ConfigurationProperties(prefix = "ontrack.config.casc")
@APIName("CasC configuration")
@APIDescription("""Configuration of the "Configuration as Code".""")
class CascConfigurationProperties {

    @APIDescription("Is the configuration as code enabled?")
    var enabled = true

    @APIDescription("List of resources to load and to monitor for changes")
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
        @APIDescription("Enables the creation of a job to reload the CasC.")
        var enabled = false

        @APIDescription("Cron schedule for the reloading. Leave blank or empty to disable the automated reloading.")
        var cron = ""
    }

    /**
     * Secrets configurations
     */
    class CascSecretConfigurationProperties {
        @APIDescription(
            """
                Source for the secrets.
                
                Either "env" (default) or "file"
            """
        )
        var type: String = "env"

        @APIDescription("""Directory used to store the secret files (used only when type == "file"""")
        var directory = ""
    }

    /**
     * Upload end point
     */
    class CascUploadConfigurationProperties {
        @APIDescription("Is the upload of Casc YAML file enabeld?")
        var enabled = false
    }

}