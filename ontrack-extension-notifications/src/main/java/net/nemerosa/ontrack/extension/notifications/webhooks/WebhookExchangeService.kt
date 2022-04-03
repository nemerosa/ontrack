package net.nemerosa.ontrack.extension.notifications.webhooks

/**
 * Storing and managing the requests & response for the webhooks.
 */
interface WebhookExchangeService {

    /**
     * Storing an exchange
     */
    fun store(webhookExchange: WebhookExchange)
}