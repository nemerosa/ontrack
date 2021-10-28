package net.nemerosa.ontrack.extension.github.ingestion.payload

import org.junit.Test

class IngestionHookSignatureTest {

    @Test
    fun `Checking a signature`() {
        IngestionHookSignature.checkPayloadSignature(
            body = "Sample payload",
            signature = "sha256=55e4e68a6fe14b9fcfb80478d26be2127bbaf04f187c6ac1441e509c990a1e52",
            token = "131413ebe7b82303ab937236e6294c03e8d53cf6",
        )
    }

}