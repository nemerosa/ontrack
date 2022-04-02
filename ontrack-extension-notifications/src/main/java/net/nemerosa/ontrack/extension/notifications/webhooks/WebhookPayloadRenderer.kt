package net.nemerosa.ontrack.extension.notifications.webhooks

/**
 * Rendering for the webhook payloads.
 */
interface WebhookPayloadRenderer {

    fun render(payload: WebhookPayload<*>): ByteArray

}