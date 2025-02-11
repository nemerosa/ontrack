package net.nemerosa.ontrack.extension.notifications.processing

import net.nemerosa.ontrack.extension.notifications.model.Notification

interface NotificationProcessingService {

    /**
     * @param item Notification to process
     * @param context Extra context to pass for the templating
     * @param outputFeedback Feedback function used to gather the output of the notification as it runs
     * @return Notification processing result
     */
    fun process(
        item: Notification,
        context: Map<String, Any>,
        outputFeedback: (recordId: String, output: Any?) -> Unit,
    ): NotificationProcessingResult<*>?

}