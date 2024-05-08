package net.nemerosa.ontrack.extension.notifications.processing

import net.nemerosa.ontrack.extension.notifications.model.Notification

interface NotificationProcessingService {

    /**
     * @param item Notification to process
     * @param context Extra context to pass for the templating
     * @return Notification output
     */
    fun process(item: Notification, context: Map<String, Any>,): Any?

}