package net.nemerosa.ontrack.extension.github.ingestion.payload

import com.fasterxml.jackson.databind.JsonNode

interface IngestionHookSignatureService {

    /**
     * Checks the GitHub hook signature and returns the body as a JSON object.
     */
    fun checkPayloadSignature(body: String, signature: String): JsonNode
}