package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import org.junit.jupiter.api.Test

class WorkflowRunIngestionEventProcessorTest {

    @Test
    fun `Payload source`() {
        val processor = WorkflowRunIngestionEventProcessor(
            structureService = mockk(),
            workflowJobProcessingService = mockk(),
            runInfoService = mockk(),
            ingestionModelAccessService = mockk(),
            configService = mockk(),
            buildIdStrategyRegistry = mockk(),
        )
        val payload = IngestionHookFixtures.sampleWorkflowRunPayload(runId = 1234L)
        kotlin.test.assertEquals(
            "1234",
            processor.getPayloadSource(payload)
        )
    }

}