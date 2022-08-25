package net.nemerosa.ontrack.extension.license

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = LicenseConfigurationProperties.PREFIX)
@Component
class LicenseConfigurationProperties {

    /**
     * License provider
     */
    var provider: String = "none"

    companion object {

        const val PREFIX = "ontrack.config.license"

    }

}