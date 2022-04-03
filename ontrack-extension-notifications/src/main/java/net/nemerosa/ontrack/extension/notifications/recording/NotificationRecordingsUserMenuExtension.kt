package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component

@Component
class NotificationRecordingsUserMenuExtension(
    extensionFeature: NotificationsExtensionFeature,
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override fun getGlobalFunction(): Class<out GlobalFunction> = NotificationRecordingAccess::class.java

    override fun getAction() = Action(
        id = "notification-recordings",
        name = "Notification recordings",
        type = ActionType.LINK,
        uri = "notification-recordings",
    )
}