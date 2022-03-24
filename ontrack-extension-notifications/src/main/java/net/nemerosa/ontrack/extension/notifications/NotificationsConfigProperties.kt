package net.nemerosa.ontrack.extension.notifications

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = NotificationsConfigProperties.PREFIX)
class NotificationsConfigProperties {

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.notifications"
    }

}