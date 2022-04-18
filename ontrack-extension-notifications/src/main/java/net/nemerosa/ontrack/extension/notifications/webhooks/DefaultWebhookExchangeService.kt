package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultWebhookExchangeService(
    private val storageService: StorageService,
) : WebhookExchangeService {

    override fun store(webhookExchange: WebhookExchange) {
        storageService.store(
            STORE,
            webhookExchange.uuid.toString(),
            webhookExchange
        )
    }

    override fun exchanges(filter: WebhookExchangeFilter): PaginatedList<WebhookExchange> {

        // JSON queries
        val jsonQueries = mutableListOf<String>()
        val jsonQueryVariables = mutableMapOf<String, String>()

        // Filter: webhook
        if (!filter.webhook.isNullOrBlank()) {
            jsonQueries += "data::jsonb->>'webhook' = :webhook"
            jsonQueryVariables["webhook"] = filter.webhook
        }

        // Filter: request type
        if (!filter.payloadType.isNullOrBlank()) {
            jsonQueries += "data::jsonb->'request'->>'type' = :payloadType"
            jsonQueryVariables["payloadType"] = filter.payloadType
        }

        // Filter: request keyword
        if (!filter.payloadKeyword.isNullOrBlank()) {
            jsonQueries += "data::jsonb->'request'->>'payload' ILIKE :payloadKeyword"
            jsonQueryVariables["payloadKeyword"] = "%${filter.payloadKeyword}%"
        }

        // Filter: request timestamp after
        if (filter.payloadAfter != null) {
            jsonQueries += "data::jsonb->'request'->>'timestamp' >= :payloadAfter"
            jsonQueryVariables["payloadAfter"] = filter.payloadAfter.asJson().asText()
        }

        // Filter: request timestamp before
        if (filter.payloadBefore != null) {
            jsonQueries += "data::jsonb->'request'->>'timestamp' <= :payloadBefore"
            jsonQueryVariables["payloadBefore"] = filter.payloadBefore.asJson().asText()
        }

        // Filter: response code
        if (filter.responseCode != null) {
            jsonQueries += "data::jsonb->'response'->>'code' = :responseCode"
            jsonQueryVariables["responseCode"] = filter.responseCode.toString()
        }

        // Filter: response keyword
        if (filter.responseKeyword != null) {
            jsonQueries += "data::jsonb->'response'->>'payload' ILIKE :responseKeyword"
            jsonQueryVariables["responseKeyword"] = "%${filter.responseKeyword}%"
        }

        // JSON queries & variables
        val jsonQuery = jsonQueries.joinToString(" AND ") { "( $it )" }

        // Total count
        val total = storageService.count(
            store = STORE,
            query = jsonQuery,
            queryVariables = jsonQueryVariables,
        )

        // Items
        val items = storageService.filter(
            store = STORE,
            type = WebhookExchange::class,
            offset = filter.offset,
            size = filter.size,
            query = jsonQuery,
            queryVariables = jsonQueryVariables,
        )

        // Page
        return PaginatedList.create(items, filter.offset, filter.size, total)
    }

    companion object {
        private val STORE = WebhookExchange::class.java.name
    }

}