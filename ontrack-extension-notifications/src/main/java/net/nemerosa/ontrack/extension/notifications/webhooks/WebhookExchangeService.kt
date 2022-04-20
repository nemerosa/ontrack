package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.pagination.PaginatedList
import java.time.LocalDateTime

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

    /**
     * Clears all deliveries created before the mentioned [time].
     */
    fun clearBefore(time: LocalDateTime)
}