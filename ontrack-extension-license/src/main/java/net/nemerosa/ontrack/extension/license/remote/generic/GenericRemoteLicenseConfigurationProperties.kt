package net.nemerosa.ontrack.extension.license.remote.generic

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties
@Component
class GenericRemoteLicenseConfigurationProperties(prefix = "ontrack.config.license.remote.generic") {

    var url: String? = null
    var username: String? = null
    var password: String? = null

}