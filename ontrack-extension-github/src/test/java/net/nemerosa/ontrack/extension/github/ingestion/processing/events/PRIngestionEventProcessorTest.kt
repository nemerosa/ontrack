package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.PullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.pr.PRPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.pr.PRPayloadAction
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PRIngestionEventProcessorTest {

    @Test
    fun `Payload source`() {
        IngestionHookFixtures;
        val processor = PRIngestionEventProcessor(
            structureService = mockk(),
            prPayloadListeners = emptyList()
        )
        assertEquals(
            "PR-50",
            processor.getPayloadSource(
                PRPayload(
                    repository = IngestionHookFixtures.sampleRepository(),
                    action = PRPayloadAction.opened,
                    pullRequest = IngestionHookFixtures.samplePullRequest(number = 50),
                )
            )
        )
    }

}