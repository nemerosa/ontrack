package net.nemerosa.ontrack.extension.notifications

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = NotificationsConfigProperties.PREFIX)
@ConstructorBinding
class NotificationsConfigProperties(
    /**
     * Are the notifications enabled?
     */
    val enabled: Boolean = true,
) {

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.config.extension.notifications"
    }

}