package net.nemerosa.ontrack.extension.license.embedded

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.config.license.embedded")
@Component
class EmbeddedLicenseConfigurationProperties {
    var key: String? = null
}
