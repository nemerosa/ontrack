package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode

/**
 * Used to actually run webhooks and their payloads.
 */
interface WebhookExecutionService {

    fun send(webhook: Webhook, payload: JsonNode)

}