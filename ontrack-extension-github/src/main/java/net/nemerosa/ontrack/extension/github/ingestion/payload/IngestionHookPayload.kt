package net.nemerosa.ontrack.extension.github.ingestion.payload

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import java.time.LocalDateTime
import java.util.*

/**
 * Payload for an ingestion to be processed.
 *
 * @property uuid Unique ID for this payload
 * @property timestamp Timestamp of reception for this payload
 * @property payload JSON payload, raw from GitHub
 */
data class IngestionHookPayload(
    val uuid: UUID = UUID.randomUUID(),
    val timestamp: LocalDateTime = Time.now(),
    val payload: JsonNode,
)
