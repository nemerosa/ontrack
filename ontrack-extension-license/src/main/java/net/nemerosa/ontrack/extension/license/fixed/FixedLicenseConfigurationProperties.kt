package net.nemerosa.ontrack.extension.license.fixed

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.config.license.fixed")
@Component
@APIName("Fixed license configuration")
class FixedLicenseConfigurationProperties {
    @APIDescription("Name of the license")
    var name: String = "n/a"
    @APIDescription("Assignee of the license")
    var assignee: String = "n/a"
    @APIDescription("Validity of the license")
    var validUntil: String? = null
    @APIDescription("Maximum number of projects")
    var maxProjects: Int = 0
    @APIDescription("Is the license active?")
    var active: Boolean = true

}