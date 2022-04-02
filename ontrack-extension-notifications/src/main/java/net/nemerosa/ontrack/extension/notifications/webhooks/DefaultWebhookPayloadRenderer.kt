package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class DefaultWebhookPayloadRenderer : WebhookPayloadRenderer {

    override fun render(payload: WebhookPayload<*>): ByteArray {
        return payload.asJson().toPrettyString().toByteArray()
    }
}