package net.nemerosa.ontrack.extension.tfc.processing

import net.nemerosa.ontrack.extension.tfc.service.TFCParameters
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ValidationRun

data class TFCNotificationProcessorResponse(
    val type: TFCNotificationProcessorResponseType,
    val message: String,
) {
    companion object {
        fun runStatusIgnored(runStatus: String) = TFCNotificationProcessorResponse(
            type = TFCNotificationProcessorResponseType.IGNORED,
            message = "Run status $runStatus is not processed."
        )

        fun validated(build: Build, validationRun: ValidationRun) = TFCNotificationProcessorResponse(
            type = TFCNotificationProcessorResponseType.OK,
            message = "${build.entityDisplayName} validated into ${validationRun.validationStamp.name}"
        )

        fun buildNotValidated(build: Build, parameters: TFCParameters) = TFCNotificationProcessorResponse(
            type = TFCNotificationProcessorResponseType.IGNORED,
            message = "${build.entityDisplayName} not validated. Parameters = $parameters"
        )

        fun buildNotFound(parameters: TFCParameters) = TFCNotificationProcessorResponse(
            type = TFCNotificationProcessorResponseType.IGNORED,
            message = "Build to validate not found. Parameters = $parameters"
        )
    }
}