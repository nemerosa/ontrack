package net.nemerosa.ontrack.extension.notifications.casc

import net.nemerosa.ontrack.extension.casc.context.AbstractHolderContext
import net.nemerosa.ontrack.extension.casc.context.extensions.SubExtensionsContext
import org.springframework.stereotype.Component

@Component
class NotificationsCascContext(
    subContexts: List<NotificationsSubCascContext>,
) : AbstractHolderContext<NotificationsSubCascContext>(
    subContexts,
    "Configuration for the notifications"
), SubExtensionsContext {
    override val field: String = "notifications"
}