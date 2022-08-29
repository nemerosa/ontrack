package net.nemerosa.ontrack.extension.license.remote.stripe

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.config.license.stripe")
@Component
class StripeLicenseConfigurationProperties {

    /**
     * API authentication token.
     */
    var token: String? = null

}