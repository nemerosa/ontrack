package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class NotificationRecordingsUserMenuItemExtension(
    extensionFeature: NotificationsExtensionFeature,
    notificationsConfigProperties: NotificationsConfigProperties,
) : AbstractExtension(extensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem> = if (notificationsConfigProperties.enabled) {
        listOf(
            UserMenuItem(
                groupId = CoreUserMenuGroups.INFORMATION,
                extension = extensionFeature,
                id = "recordings",
                name = "Notification records",
            )
        )
    } else {
        emptyList()
    }

}