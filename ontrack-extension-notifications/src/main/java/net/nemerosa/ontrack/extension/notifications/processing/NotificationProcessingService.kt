package net.nemerosa.ontrack.extension.notifications.processing

import net.nemerosa.ontrack.extension.notifications.model.Notification

interface NotificationProcessingService {

    /**
     * @param item Notification to process
     * @return Notification output
     */
    fun process(item: Notification): Any?

}