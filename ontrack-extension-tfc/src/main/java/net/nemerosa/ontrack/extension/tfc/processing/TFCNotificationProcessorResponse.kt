package net.nemerosa.ontrack.extension.tfc.processing

data class TFCNotificationProcessorResponse(
    val type: TFCNotificationProcessorResponseType,
    val message: String,
) {
    companion object {
        fun runStatusIgnored(runStatus: String) = TFCNotificationProcessorResponse(
            type = TFCNotificationProcessorResponseType.IGNORED,
            message = "Run status $runStatus is not processed."
        )
    }
}