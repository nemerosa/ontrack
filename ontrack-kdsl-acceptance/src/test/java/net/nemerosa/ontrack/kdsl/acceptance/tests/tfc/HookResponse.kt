package net.nemerosa.ontrack.kdsl.acceptance.tests.tfc

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.acceptance.tests.queue.QueueDispatchResult
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
        @Deprecated("Prefer using infoLink")
        val info: JsonNode,
        val infoLink: HookInfoLink?,
) {
    @get:JsonIgnore
    val queueID: String
        get() =
            if (infoLink != null && infoLink.feature == "tfc" && infoLink.id == "tfc" && infoLink.data.isArray) {
                val results = infoLink.data.map {
                    it.parse<QueueDispatchResult>()
                }
                results.firstOrNull()?.id ?: fail("No queue ID returned: $infoLink")
            } else {
                fail("No queue ID returned: $infoLink")
            }
}
