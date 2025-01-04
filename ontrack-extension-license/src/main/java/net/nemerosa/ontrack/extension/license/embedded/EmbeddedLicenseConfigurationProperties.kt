package net.nemerosa.ontrack.extension.license.embedded

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.config.license.embedded")
@Component
@APIName("Embedded license configuration")
class EmbeddedLicenseConfigurationProperties {
    @APIDescription("License key")
    var key: String? = null
}
