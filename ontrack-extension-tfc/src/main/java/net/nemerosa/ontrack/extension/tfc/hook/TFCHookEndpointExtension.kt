package net.nemerosa.ontrack.extension.tfc.hook

import net.nemerosa.ontrack.extension.hook.*
import net.nemerosa.ontrack.extension.hook.queue.toHookResponse
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatchResult
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatchResultType
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.tfc.TFCConfigProperties
import net.nemerosa.ontrack.extension.tfc.TFCExtensionFeature
import net.nemerosa.ontrack.extension.tfc.queue.TFCQueueProcessor
import net.nemerosa.ontrack.extension.tfc.service.RunPayload
import net.nemerosa.ontrack.extension.tfc.service.RunPayloadMissingFieldException
import net.nemerosa.ontrack.extension.tfc.service.TFCParameters
import net.nemerosa.ontrack.extension.tfc.settings.TFCSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty0

@Component
class TFCHookEndpointExtension(
    private val cachedSettingsService: CachedSettingsService,
    private val tfcConfigProperties: TFCConfigProperties,
    private val queueDispatcher: QueueDispatcher,
    private val queueProcessor: TFCQueueProcessor,
    extensionFeature: TFCExtensionFeature,
) : AbstractExtension(extensionFeature), HookEndpointExtension {

    private val logger: Logger = LoggerFactory.getLogger(TFCHookEndpointExtension::class.java)

    override val id: String = "tfc"

    override val enabled: Boolean
        get() = cachedSettingsService.getCachedSettings(TFCSettings::class.java).enabled

    override fun checkAccess(request: HookRequest) {
        if (tfcConfigProperties.hook.signature.disabled) {
            logger.warn("TFC Hook signature checks are disabled.")
        } else {
            val token = cachedSettingsService.getCachedSettings(TFCSettings::class.java).token
            HookSignature.checkSignature(
                request.body,
                request.getRequiredHeader("X-TFE-Notification-Signature"),
                token
            )
        }
    }

    override fun process(request: HookRequest): HookResponse {
        // Getting the parameters from the URL
        val parameters = request.parseParameters<TFCParameters>()
        // Parsing the body
        val payload = request.parseBodyAsJson<TFCHookPayload>()
        // Processes each notification separately
        val results = payload.notifications.map { notification ->
            processNotification(parameters, payload, notification)
        }
        // OK
        return results.toHookResponse()
    }

    private fun processNotification(
        parameters: TFCParameters,
        payload: TFCHookPayload,
        notification: TFCHookPayloadNotification
    ): QueueDispatchResult = when (notification.trigger) {
        "run:completed" -> processRun(parameters, payload, notification)
        "run:errored" -> processRun(parameters, payload, notification)
        else -> ignoredTrigger(notification)
    }

    private fun processRun(
        parameters: TFCParameters,
        hook: TFCHookPayload,
        notification: TFCHookPayloadNotification
    ): QueueDispatchResult {
        // Queue payload
        val payload = RunPayload(
            parameters = parameters,
            runUrl = payload(hook::runUrl),
            runId = payload(hook::runId),
            workspaceId = payload(hook::workspaceId),
            workspaceName = payload(hook::workspaceName),
            organizationName = payload(hook::organizationName),
            message = payload(notification::message),
            trigger = payload(notification::trigger),
            runStatus = payload(notification::runStatus),
        )
        // Launching the processing on a queue dispatcher
        return queueDispatcher.dispatch(queueProcessor, payload)
    }

    private fun payload(property: KProperty0<String?>): String {
        val value = property.get()
        if (value != null) {
            return value
        } else {
            throw RunPayloadMissingFieldException(property.name)
        }
    }

    private fun ignoredTrigger(notification: TFCHookPayloadNotification) = QueueDispatchResult(
        type = QueueDispatchResultType.IGNORED,
        message = "Notification trigger ${notification.trigger} is not processed",
        id = null,
    )

    private data class HookNotificationProcessing(
        val type: HookResponseType,
        val message: String?,
        val id: String?,
    )
}