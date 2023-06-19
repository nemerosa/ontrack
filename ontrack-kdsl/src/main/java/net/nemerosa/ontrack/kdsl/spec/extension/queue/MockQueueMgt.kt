package net.nemerosa.ontrack.kdsl.spec.extension.queue

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.PostQueueMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class MockQueueMgt(connector: Connector) : Connected(connector) {

    /**
     * Posts a message for the mock queue processor.
     *
     * @param message Payload
     * @return ID of the queue record
     */
    fun post(message: String): String =
            graphqlConnector.mutate(
                    PostQueueMutation(
                            "mock",
                            mapOf("message" to message).asJson()
                    )
            ) {
                it?.postQueue()?.fragments()?.payloadUserErrors()?.convert()
            }
                    ?.checkData { it.postQueue()?.queueDispatchResult() }
                    ?.id()
                    ?: error("Could not post on the queue")

}