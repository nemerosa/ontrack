package net.nemerosa.ontrack.kdsl.acceptance.tests.tfc

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlin.test.fail

/**
 * Data returned by the TFC hook.
 */
data class HookResponse(
    /**
     * Type of response
     */
    val type: String,
    /**
     *
     */
    val info: List<QueueDispatchResult>,
) {
    @get:JsonIgnore
    val queueID: String
        get() =
            info.firstOrNull()?.id
                ?: fail("No queue ID returned")
}
