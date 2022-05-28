package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class IngestionLinksEventProcessorTest {

    @Test
    fun `Payload source for the ingestion of a links event with run id`() {
        val processor = mockProcessor()

        assertEquals(
            "run=12345",
            processor.getPayloadSource(
                GitHubIngestionLinksPayload(
                    "owner",
                    "repository",
                    runId = 12345,
                    buildLinks = emptyList(),
                    addOnly = true,
                )
            )
        )
    }

    @Test
    fun `Payload source for the ingestion of a links event with build name`() {
        val processor = mockProcessor()

        assertEquals(
            "name=name",
            processor.getPayloadSource(
                GitHubIngestionLinksPayload(
                    "owner",
                    "repository",
                    buildName = "name",
                    buildLinks = emptyList(),
                    addOnly = true,
                )
            )
        )
    }

    @Test
    fun `Payload source for the ingestion of a links event with build label`() {
        val processor = mockProcessor()

        assertEquals(
            "label=1.0.0",
            processor.getPayloadSource(
                GitHubIngestionLinksPayload(
                    "owner",
                    "repository",
                    buildLabel = "1.0.0",
                    buildLinks = emptyList(),
                    addOnly = true,
                )
            )
        )
    }

    private fun mockProcessor() = IngestionLinksEventProcessor(
        ingestionModelAccessService = mockk(),
        structureService = mockk(),
    )

}