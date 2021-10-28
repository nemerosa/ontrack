package net.nemerosa.ontrack.extension.github.ingestion

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookSignatureService
import net.nemerosa.ontrack.json.parseAsJson

/**
 * No check.
 */
class MockIngestionHookSignatureService : IngestionHookSignatureService {
    override fun checkPayloadSignature(body: String, signature: String): JsonNode {
        return body.parseAsJson()
    }
}