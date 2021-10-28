package net.nemerosa.ontrack.extension.github.ingestion

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.json.asJson
import java.util.*

object IngestionHookFixtures {

    /**
     * Sample payload
     */
    fun payload(): IngestionHookPayload =
        payloadHeaders().run {
            IngestionHookPayload(
                gitHubDelivery = gitHubDelivery,
                gitHubEvent = gitHubEvent,
                gitHubHookID = gitHubHookID,
                gitHubHookInstallationTargetID = gitHubHookInstallationTargetID,
                gitHubHookInstallationTargetType = gitHubHookInstallationTargetType,
                payload = payloadBody(),
            )
        }

    /**
     * Sample payload body
     */
    fun payloadBody(): JsonNode {
        return mapOf(
            "id" to UUID.randomUUID().toString(),
            "test" to "value"
        ).asJson()
    }

    /**
     * Sample headers for a hook request
     */
    fun payloadHeaders() = Headers(
        gitHubDelivery = UUID.randomUUID().toString(),
        gitHubEvent = "ping",
        gitHubHookID = 123456,
        gitHubHookInstallationTargetID = 1234567890,
        gitHubHookInstallationTargetType = "repository",
    )

    /**
     * Headers needed for a hook
     */
    class Headers(
        val gitHubDelivery: String,
        val gitHubEvent: String,
        val gitHubHookID: Int,
        val gitHubHookInstallationTargetID: Int,
        val gitHubHookInstallationTargetType: String,
    )

}