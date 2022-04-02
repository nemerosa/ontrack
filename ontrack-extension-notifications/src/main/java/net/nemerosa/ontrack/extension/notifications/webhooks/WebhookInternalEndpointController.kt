package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Internal endpoint for simulating a webhook.
 *
 * Should be used only for testing.
 */
@RestController
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.notifications.webhook.internal",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false,
)
@RequestMapping("/extension/notifications/webhooks/internal")
class WebhookInternalEndpointController {

    /**
     * Storing the payloads in memory
     */
    private val payloads = mutableMapOf<UUID, JsonWebhookPayload>()

    /**
     * The endpoint.
     */
    @PostMapping("")
    fun post(@RequestBody payload: JsonWebhookPayload): String {
        payloads[payload.uuid] = payload
        return "OK"
    }

    /**
     * Gets the list of payloads
     */
    @GetMapping("payloads")
    fun payloads() = payloads.toMap()

    /**
     * Json payload
     */
    data class JsonWebhookPayload(
        val uuid: UUID,
        val type: String,
        val data: JsonNode,
    )

}