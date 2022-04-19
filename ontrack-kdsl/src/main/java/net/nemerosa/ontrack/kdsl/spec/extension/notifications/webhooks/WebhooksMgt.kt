package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateWebhookMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.connector.support.PaginatedList
import java.time.Duration

/**
 * Interface for the management of webhooks in Ontrack.
 */
class WebhooksMgt(connector: Connector) : Connected(connector) {

    /**
     * Registers a webhook.
     *
     * @param name Unique name for the webhook
     * @param enabled If the webhook is enabled
     * @param url Endpoint URL for the webhook
     * @param timeout Timeout for the webhook execution
     * @param authenticationType Authentication type
     * @param authenticationConfig Authentication configuration
     */
    fun createWebhook(
        name: String,
        enabled: Boolean,
        url: String,
        timeout: Duration,
        authenticationType: String,
        authenticationConfig: Any,
    ) {
        graphqlConnector.mutate(
            CreateWebhookMutation(
                name,
                enabled,
                url,
                timeout.toSeconds(),
                authenticationType,
                authenticationConfig.asJson(),
            )
        ) {
            it?.createWebhook()?.fragments()?.payloadUserErrors()?.convert()
        }
    }

    /**
     * Pings an existing webhook
     */
    fun ping(name: String) {
        connector.post("/extension/notifications/webhook/$name/ping")
    }

    /**
     * Gets the deliveries for the webhooks.
     *
     * @param offset Offset in the list
     * @param size Size of a page
     * @param webhook Filter on the name of the webhook
     */
    fun getDeliveries(
        offset: Int = 0,
        size: Int = 10,
        webhook: String,
    ): PaginatedList<WebhookDelivery> =
        TODO()
//        graphqlConnector.query(
//            WebhookDeliveriesQuery(
//                Input.fromNullable(offset),
//                Input.fromNullable(size),
//                webhook
//            )
//        )?.paginate(
//            pageInfo = { it.webhooks().firstOrNull()?.exchanges()?.pageInfo()?.fragments()?.pageInfoContent() },
//            pageItems = { it.webhooks().firstOrNull()?.exchanges()?.pageItems() }
//        )?.map {
//            TODO("Missing mapping to LocalDateTime & UUID")
//            // WebhookDelivery(
//            //     uuid = UUID.fromString(it.uuid().toString()),
//            //     webhook = it.webhook(),
//            //     request = WebhookRequest(
//            //         timestamp = it.request().timestamp()
//            //     )
//            // )
//        } ?: emptyPaginatedList()

    /**
     * Internal endpoint
     */
    val internalEndpoint: InternalEndpointMgt by lazy {
        InternalEndpointMgt(connector)
    }

}