package net.nemerosa.ontrack.extension.license.fixed

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@ConfigurationProperties(prefix = "ontrack.config.license.fixed")
@Component
class FixedLicenseConfigurationProperties {

    var name: String = "n/a"
    var assignee: String = "n/a"
    var validUntil: String? = null
    var maxProjects: Int = 0
    var active: Boolean = true

}