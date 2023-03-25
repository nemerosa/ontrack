package net.nemerosa.ontrack.extension.tfc.hook.dispatching

import net.nemerosa.ontrack.extension.tfc.hook.TFCHookNotificationResponse
import net.nemerosa.ontrack.extension.tfc.hook.TFCHookParameters
import net.nemerosa.ontrack.extension.tfc.hook.TFCHookResponse
import net.nemerosa.ontrack.extension.tfc.hook.TFCHookResponseType
import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayload
import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayloadNotification
import net.nemerosa.ontrack.extension.tfc.processing.TFCNotificationProcessor
import net.nemerosa.ontrack.extension.tfc.processing.TFCNotificationProcessorRegistry
import net.nemerosa.ontrack.extension.tfc.processing.TFCNotificationProcessorResponse
import net.nemerosa.ontrack.extension.tfc.processing.TFCNotificationProcessorResponseType
import org.springframework.stereotype.Component

@Component
class TFCHookDispatcherImpl(
    private val tfcNotificationProcessorRegistry: TFCNotificationProcessorRegistry
) : TFCHookDispatcher {

    override fun dispatch(parameters: TFCHookParameters, payload: TFCHookPayload): TFCHookResponse {
        // Processing of each notification
        val responses = payload.notifications.map { notification ->
            dispatchNotification(parameters, payload, notification)
        }
        // Consolidates all responses
        return TFCHookResponse.all(responses)
    }

    private fun dispatchNotification(
        parameters: TFCHookParameters,
        payload: TFCHookPayload,
        notification: TFCHookPayloadNotification
    ): TFCHookNotificationResponse {
        val trigger = notification.trigger
        // Gets a processor for this trigger
        val processor = tfcNotificationProcessorRegistry.findProcessorByTrigger<Any>(trigger)
            ?: return TFCHookNotificationResponse.processorNotFound(trigger)
        // Launching the processing
        val response = processing(processor, parameters, payload, notification)
        // Conversion of the response
        return response.toHookNotificationResponse()
    }

    private fun TFCNotificationProcessorResponse.toHookNotificationResponse() = TFCHookNotificationResponse(
        type = when (type) {
            TFCNotificationProcessorResponseType.IGNORED -> TFCHookResponseType.IGNORED
        },
        message = null,
    )

    private fun <P> processing(
        processor: TFCNotificationProcessor<P>,
        parameters: TFCHookParameters,
        payload: TFCHookPayload,
        notification: TFCHookPayloadNotification
    ): TFCNotificationProcessorResponse {
        // Conversion to the final payload
        val processingPayload = processor.convertFromHook(payload, notification)
        // Conversion of parameters
        val serviceParams = parameters.toServiceParameters()
        // Processing
        return processor.process(serviceParams, processingPayload)
    }

}
