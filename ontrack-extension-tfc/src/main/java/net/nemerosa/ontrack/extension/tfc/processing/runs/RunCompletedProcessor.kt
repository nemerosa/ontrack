package net.nemerosa.ontrack.extension.tfc.processing.runs

import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayload
import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayloadNotification
import net.nemerosa.ontrack.extension.tfc.processing.TFCNotificationProcessor
import net.nemerosa.ontrack.extension.tfc.processing.TFCNotificationProcessorResponse
import net.nemerosa.ontrack.extension.tfc.service.TFCParameters
import net.nemerosa.ontrack.extension.tfc.service.TFCService
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty0

@Component
class RunCompletedProcessor(
    private val tfcService: TFCService,
) : TFCNotificationProcessor<RunPayload> {

    override val trigger: String = "run:completed"

    override fun convertFromHook(hook: TFCHookPayload, notification: TFCHookPayloadNotification) = RunPayload(
        runUrl = payload(hook::runUrl),
        runId = payload(hook::runId),
        workspaceId = payload(hook::workspaceId),
        workspaceName = payload(hook::workspaceName),
        organizationName = payload(hook::organizationName),
        message = payload(notification::message),
        trigger = payload(notification::trigger),
        runStatus = payload(notification::runStatus),
    )

    private fun payload(property: KProperty0<String?>): String {
        val value = property.get()
        if (value != null) {
            return value
        } else {
            throw RunPayloadMissingFieldException(property.name)
        }
    }

    override fun process(
        params: TFCParameters,
        processingPayload: RunPayload,
    ): TFCNotificationProcessorResponse =
        when (processingPayload.runStatus) {
            "applied" -> applied(params, processingPayload)
            else -> TFCNotificationProcessorResponse.runStatusIgnored(processingPayload.runStatus)
        }

    private fun applied(
        params: TFCParameters,
        processingPayload: RunPayload,
    ): TFCNotificationProcessorResponse {
        val result = tfcService.validate(
            params,
            ValidationRunStatusID.STATUS_PASSED,
            processingPayload.workspaceId,
            processingPayload.runUrl,
        )
        return if (result.build != null) {
            if (result.validationRun != null) {
                TFCNotificationProcessorResponse.validated(result.build, result.validationRun)
            } else {
                TFCNotificationProcessorResponse.buildNotValidated(result.build, result.parameters)
            }
        } else {
            TFCNotificationProcessorResponse.buildNotFound(result.parameters)
        }
    }
}