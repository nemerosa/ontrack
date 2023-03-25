package net.nemerosa.ontrack.extension.tfc.hook

/**
 * Response for the TFC Hook.
 */
data class TFCHookNotificationResponse(
    val type: TFCHookResponseType,
    val message: String?,
) {
    companion object {
        fun processorNotFound(trigger: String) = TFCHookNotificationResponse(
            type = TFCHookResponseType.IGNORED,
            message = "Trigger $trigger not managed."
        )
    }
}
