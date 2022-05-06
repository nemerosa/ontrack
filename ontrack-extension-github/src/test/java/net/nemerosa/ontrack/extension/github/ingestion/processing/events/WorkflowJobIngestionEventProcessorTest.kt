package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class WorkflowJobIngestionEventProcessorTest {

    @Test
    fun `Payload source`() {
        val processor = WorkflowJobIngestionEventProcessor(
            structureService = mockk(),
            workflowJobProcessingService = mockk(),
        )
        val payload = IngestionHookFixtures.sampleWorkflowJobPayload(
            jobName = "my-job"
        )
        assertEquals(
            "my-job",
            processor.getPayloadSource(payload)
        )
    }

}