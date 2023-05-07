package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.model.extension.Extension

/**
 * Extension to define a hook.
 */
interface HookEndpointExtension : Extension {

    /**
     * ID of the hook endpoint, used for the URL, like
     * in `/hook/secured/<hook>`.
     */
    val id: String

    /**
     * Is this endpoint enabled?
     */
    val enabled: Boolean

    /**
     * Checks the access to endpoint.
     *
     * Throws an exception if case the access is denied.
     */
    fun checkAccess(request: HookRequest)

    /**
     * Processes the hook request.
     */
    fun process(request: HookRequest): HookResponse

}