package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerOutcome
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PushIngestionEventProcessorTest {

    private lateinit var processor: PushIngestionEventProcessor

    @Before
    fun before() {
        val listenerOnPath1 = mockk<PushPayloadListener>()
        every {
            listenerOnPath1.process(any())
        } answers {
            val payload: PushPayload = args[0] as PushPayload
            if (payload.isAddedOrModified("path/1")) {
                PushPayloadListenerOutcome.PROCESSED
            } else {
                PushPayloadListenerOutcome.IGNORED
            }
        }
        val listenerOnPath2 = mockk<PushPayloadListener>()
        every {
            listenerOnPath2.process(any())
        } answers {
            val payload: PushPayload = args[0] as PushPayload
            if (payload.isAddedOrModified("path/2")) {
                PushPayloadListenerOutcome.PROCESSED
            } else {
                PushPayloadListenerOutcome.IGNORED
            }
        }
        processor = PushIngestionEventProcessor(
            structureService = mockk(),
            pushPayloadListeners = listOf(
                listenerOnPath1,
                listenerOnPath2,
            )
        )
    }

    @Test
    fun `Path 1 pushed`() {
        assertEquals(
            IngestionEventProcessingResult.PROCESSED,
            processor.process(payload("path/1"))
        )
    }

    @Test
    fun `Path 1 and other pushed`() {
        assertEquals(
            IngestionEventProcessingResult.PROCESSED,
            processor.process(payload("path/1", "path/other"))
        )
    }

    @Test
    fun `Path 2 pushed`() {
        assertEquals(
            IngestionEventProcessingResult.PROCESSED,
            processor.process(payload("path/2"))
        )
    }

    @Test
    fun `Path 1 and 2 pushed`() {
        assertEquals(
            IngestionEventProcessingResult.PROCESSED,
            processor.process(payload("path/1", "path/2"))
        )
    }

    @Test
    fun `Neither path 1 and 2 pushed`() {
        assertEquals(
            IngestionEventProcessingResult.IGNORED,
            processor.process(payload("path/other", "other"))
        )
    }

    private fun payload(
        vararg paths: String,
    ) = PushPayload(
        repository = IngestionHookFixtures.sampleRepository(),
        ref = "refs/heads/${IngestionHookFixtures.sampleBranch}",
        commits = listOf(
            Commit(
                id = "commit",
                message = "Commit",
                author = IngestionHookFixtures.sampleAuthor(),
                added = paths.toList(),
                modified = emptyList(),
                removed = emptyList(),
            ),
        )
    )

}