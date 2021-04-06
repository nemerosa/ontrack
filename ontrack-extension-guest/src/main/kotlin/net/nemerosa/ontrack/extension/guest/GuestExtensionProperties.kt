package net.nemerosa.ontrack.extension.guest

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Configuration options for the guest account
 */
@ConfigurationProperties(prefix = "ontrack.config.extension.guest")
@Component
class GuestExtensionProperties {

    /**
     * Is the guest account enabled?
     */
    var enabled: Boolean = false

    /**
     * Guest account user name
     */
    var username: String = "guest"

    /**
     * Full name of the guest account
     */
    var fullname: String = "Guest"

    /**
     * Guest account password
     */
    var password: String = "guest"

    /**
     * Must the guest account be displayed on the login page?
     */
    var display: Boolean = true

}