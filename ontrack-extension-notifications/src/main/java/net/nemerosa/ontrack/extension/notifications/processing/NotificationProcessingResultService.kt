package net.nemerosa.ontrack.extension.notifications.processing

import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult

interface NotificationProcessingResultService {

    fun getActualizedResult(processingResult: NotificationProcessingResult<*>): NotificationProcessingResult<*>?
    fun getActualizedResult(recordId: String): NotificationResult<*>?

}