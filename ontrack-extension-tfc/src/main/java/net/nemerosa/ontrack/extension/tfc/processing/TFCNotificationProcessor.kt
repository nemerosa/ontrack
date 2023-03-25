package net.nemerosa.ontrack.extension.tfc.processing

import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayload
import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayloadNotification
import net.nemerosa.ontrack.extension.tfc.service.TFCParameters

interface TFCNotificationProcessor<P> {

    /**
     * Notification trigger to process.
     */
    val trigger: String

    /**
     * Conversion from the hook payload
     */
    fun convertFromHook(hook: TFCHookPayload, notification: TFCHookPayloadNotification): P

    /**
     * Processing
     */
    fun process(params: TFCParameters, processingPayload: P): TFCNotificationProcessorResponse

}