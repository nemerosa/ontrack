package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookSignatureCheckResult
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookSignatureService

/**
 * No check.
 */
class MockIngestionHookSignatureService(
    private val result: IngestionHookSignatureCheckResult = IngestionHookSignatureCheckResult.OK,
) : IngestionHookSignatureService {
    override fun checkPayloadSignature(body: String, signature: String): IngestionHookSignatureCheckResult = result
}