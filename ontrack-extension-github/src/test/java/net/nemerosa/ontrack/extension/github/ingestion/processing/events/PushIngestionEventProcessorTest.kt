package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerCheck
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PushIngestionEventProcessorTest {

    private lateinit var processor: PushIngestionEventProcessor

    @Before
    fun before() {
        val listenerOnPath1 = mockk<PushPayloadListener>()
        every {
            listenerOnPath1.preProcessCheck(any())
        } answers {
            val payload: PushPayload = args[0] as PushPayload
            if (payload.isAddedOrModified("path/1")) {
                PushPayloadListenerCheck.TO_BE_PROCESSED
            } else {
                PushPayloadListenerCheck.IGNORED
            }
        }
        val listenerOnPath2 = mockk<PushPayloadListener>()
        every {
            listenerOnPath2.preProcessCheck(any())
        } answers {
            val payload: PushPayload = args[0] as PushPayload
            if (payload.isAddedOrModified("path/2")) {
                PushPayloadListenerCheck.TO_BE_PROCESSED
            } else {
                PushPayloadListenerCheck.IGNORED
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
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED,
            processor.preProcessingCheck(payload("path/1"))
        )
    }

    @Test
    fun `Path 1 and other pushed`() {
        assertEquals(
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED,
            processor.preProcessingCheck(payload("path/1", "path/other"))
        )
    }

    @Test
    fun `Path 2 pushed`() {
        assertEquals(
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED,
            processor.preProcessingCheck(payload("path/2"))
        )
    }

    @Test
    fun `Path 1 and 2 pushed`() {
        assertEquals(
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED,
            processor.preProcessingCheck(payload("path/1", "path/2"))
        )
    }

    @Test
    fun `Neither path 1 and 2 pushed`() {
        assertEquals(
            IngestionEventPreprocessingCheck.IGNORED,
            processor.preProcessingCheck(payload("path/other", "other"))
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