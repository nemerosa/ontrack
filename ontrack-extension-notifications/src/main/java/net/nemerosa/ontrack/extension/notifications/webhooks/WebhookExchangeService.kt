package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.pagination.PaginatedList

/**
 * Storing and managing the requests & response for the webhooks.
 */
interface WebhookExchangeService {

    /**
     * Storing an exchange
     */
    fun store(webhookExchange: WebhookExchange)

    /**
     * Getting a list of exchanges
     */
    fun exchanges(filter: WebhookExchangeFilter): PaginatedList<WebhookExchange>
}