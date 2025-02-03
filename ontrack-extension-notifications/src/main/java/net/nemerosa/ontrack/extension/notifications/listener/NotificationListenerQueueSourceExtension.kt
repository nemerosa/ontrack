package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.queue.source.QueueSourceExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class NotificationListenerQueueSourceExtension(
    notificationsExtensionFeature: NotificationsExtensionFeature,
) : AbstractExtension(notificationsExtensionFeature), QueueSourceExtension<NotificationListenerQueueSourceData> {

    override val id: String = "event"

}