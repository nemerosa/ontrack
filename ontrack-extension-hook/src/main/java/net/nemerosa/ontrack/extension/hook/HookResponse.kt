package net.nemerosa.ontrack.extension.hook

/**
 * Data returned by a hook.
 */
data class HookResponse(
    /**
     * Type of response
     */
    val type: HookResponseType,
    /**
     * Additional information (non structured, will typically be rendered as JSON)
     */
    val info: Any?,
)
