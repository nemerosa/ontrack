package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
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
     */
    fun createWebhook(
        name: String,
        enabled: Boolean,
        url: String,
        timeout: Duration,
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Internal endpoint
     */
    val internalEndpoint: InternalEndpointMgt by lazy {
        InternalEndpointMgt(connector)
    }

}