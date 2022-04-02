package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateWebhookMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
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
     * Internal endpoint
     */
    val internalEndpoint: InternalEndpointMgt by lazy {
        InternalEndpointMgt(connector)
    }

}