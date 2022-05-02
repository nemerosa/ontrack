package net.nemerosa.ontrack.extension.notifications.webhooks

/**
 * Used to actually run webhooks and their payloads.
 */
interface WebhookExecutionService {

    fun send(webhook: Webhook, payload: WebhookPayload<*>)

}