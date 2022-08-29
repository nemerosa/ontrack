package net.nemerosa.ontrack.extension.license.remote.licensespring

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.config.license.licensespring")
@Component
class LicenseSpringLicenseServiceConfigurationProperties {

    var key: String? = null

    var management = Management()

    data class Management(
        var key: String? = null,
    )

}