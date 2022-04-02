package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class InternalEndpointPayload(
    val uuid: UUID,
    val type: String,
    val data: JsonNode,
)
