package net.nemerosa.ontrack.kdsl.acceptance.tests.tfc

import com.fasterxml.jackson.databind.JsonNode

/**
 * Data returned by a hook.
 */
data class HookResponse(
    /**
     * Type of response
     */
    val type: String,
    /**
     * Additional information (non structured, will typically be rendered as JSON)
     */
    val info: JsonNode?,
)
