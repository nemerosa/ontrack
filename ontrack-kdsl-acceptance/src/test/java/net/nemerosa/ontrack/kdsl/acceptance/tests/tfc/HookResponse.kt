package net.nemerosa.ontrack.kdsl.acceptance.tests.tfc

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parseOrNull
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
    val info: JsonNode,
) {
    @get:JsonIgnore
    val queueID: String
        get() =
            if (info.isTextual) {
                val message = info.asText()
                fail(message)
            } else if (info.isArray) {
                val results = info.mapNotNull {
                    it.parseOrNull<QueueDispatchResult>()
                }
                results.firstOrNull()?.id ?: fail("No queue ID returned: $info")
            } else {
                fail("No queue ID returned: $info")
            }
}
