package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector

/**
 * Interface for the test internal endpoint
 */
class InternalEndpointMgt(connector: Connector) : Connected(connector) {

    /**
     * Gets the list of received payloads
     */
    val payloads: List<InternalEndpointPayload>
        get() = connector.get("/extension/notifications/webhooks/internal/payloads")
            .body.asJson().map {
                it.parse()
            }

}