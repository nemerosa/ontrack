package net.nemerosa.ontrack.extension.tfc.hook

/**
 * Response for the TFC Hook.
 */
data class TFCHookResponse(
    val type: TFCHookResponseType,
    val message: String?,
    val notifications: List<TFCHookNotificationResponse>,
) {
    companion object {
        fun all(responses: List<TFCHookNotificationResponse>) = TFCHookResponse(
            type = TFCHookResponseType.all(responses.map { it.type }),
            message = null,
            notifications = responses,
        )
    }
}
