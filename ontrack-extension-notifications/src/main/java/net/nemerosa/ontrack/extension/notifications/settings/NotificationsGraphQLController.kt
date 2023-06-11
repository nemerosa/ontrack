package net.nemerosa.ontrack.extension.notifications.settings

import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class NotificationsGraphQLController(
    private val notificationsConfigProperties: NotificationsConfigProperties,
) {

    @QueryMapping
    fun notificationSettings() = NotificationSettings(
        enabled = notificationsConfigProperties.enabled,
    )

}