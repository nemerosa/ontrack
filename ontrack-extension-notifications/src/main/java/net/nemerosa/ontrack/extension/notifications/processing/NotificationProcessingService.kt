package net.nemerosa.ontrack.extension.notifications.processing

import net.nemerosa.ontrack.extension.notifications.model.Notification

interface NotificationProcessingService {

    fun process(item: Notification)

}