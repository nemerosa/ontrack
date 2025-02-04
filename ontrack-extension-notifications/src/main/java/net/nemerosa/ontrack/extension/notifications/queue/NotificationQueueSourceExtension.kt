package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.queue.source.QueueSourceExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class NotificationQueueSourceExtension(
    notificationsExtensionFeature: NotificationsExtensionFeature,
) : AbstractExtension(notificationsExtensionFeature), QueueSourceExtension<NotificationQueueSourceData> {

    override val id: String = "notification-processing"

}