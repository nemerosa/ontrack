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
     *
     * @return An Ontrack token which is used to target an account to run the hook
     */
    fun checkAccess(request: HookRequest): String

    /**
     * Processes the hook request.
     *
     * @param recordId Unique ID for the request
     * @param request Request to process
     * @return Hook processing response
     */
    fun process(recordId: String, request: HookRequest): HookResponse

}