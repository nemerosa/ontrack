package net.nemerosa.ontrack.extension.github.ingestion.payload

import com.fasterxml.jackson.databind.JsonNode

interface IngestionHookSignatureService {

    /**
     * Checks the GitHub hook signature.
     *
     * @param body Raw body of the payload
     * @param signature Signature for the payload
     * @return Result of the signature check
     */
    fun checkPayloadSignature(body: String, signature: String): IngestionHookSignatureCheckResult
}