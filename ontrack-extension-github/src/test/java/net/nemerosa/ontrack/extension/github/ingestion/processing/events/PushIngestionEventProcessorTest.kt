package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerCheck
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PushIngestionEventProcessorTest {

    private lateinit var processor: PushIngestionEventProcessor

    private val processedPaths = mutableSetOf<String>()

    @BeforeEach
    fun before() {
        processedPaths.clear()

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
        every {
            listenerOnPath1.process(any(), any())
        } answers {
            processedPaths += "path/1"
            IngestionEventProcessingResultDetails.processed()
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
        every {
            listenerOnPath2.process(any(), any())
        } answers {
            processedPaths += "path/2"
            IngestionEventProcessingResultDetails.processed()
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
    fun `Payload source for a tag`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            ref = "refs/tags/1.0.0"
        )
        assertEquals(
            "1.0.0",
            processor.getPayloadSource(payload)
        )
    }

    @Test
    fun `Payload source for a branch with multiple updates`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            ref = "refs/heads/main",
            id = "1234567",
            added = listOf("added"),
            removed = listOf("removed"),
            modified = listOf("modified"),
        )
        assertEquals(
            "main@1234567",
            processor.getPayloadSource(payload)
        )
    }

    @Test
    fun `Payload source for a branch with one updated path`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            ref = "refs/heads/main",
            id = "1234567",
            modified = listOf(".github/ontrack/auto-versioning.yml"),
        )
        assertEquals(
            ".github/ontrack/auto-versioning.yml",
            processor.getPayloadSource(payload)
        )
    }

    @Test
    fun `Path 1 pushed`() {
        val payload = payload("path/1")
        assertEquals(
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED,
            processor.preProcessingCheck(payload)
        )
        assertEquals(
            IngestionEventProcessingResult.PROCESSED,
            processor.process(payload, null).result
        )
        assertEquals(
            setOf("path/1"),
            processedPaths
        )
    }

    @Test
    fun `Path 1 and other pushed`() {
        val payload = payload("path/1", "path/other")
        assertEquals(
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED,
            processor.preProcessingCheck(payload)
        )
        assertEquals(
            IngestionEventProcessingResult.PROCESSED,
            processor.process(payload, null).result
        )
        assertEquals(
            setOf("path/1"),
            processedPaths
        )
    }

    @Test
    fun `Path 2 pushed`() {
        val payload = payload("path/2")
        assertEquals(
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED,
            processor.preProcessingCheck(payload)
        )
        assertEquals(
            IngestionEventProcessingResult.PROCESSED,
            processor.process(payload, null).result
        )
        assertEquals(
            setOf("path/2"),
            processedPaths
        )
    }

    @Test
    fun `Path 1 and 2 pushed`() {
        val payload = payload("path/1", "path/2")
        assertEquals(
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED,
            processor.preProcessingCheck(payload)
        )
        assertEquals(
            IngestionEventProcessingResult.PROCESSED,
            processor.process(payload, null).result
        )
        assertEquals(
            setOf("path/1", "path/2"),
            processedPaths
        )
    }

    @Test
    fun `Neither path 1 and 2 pushed`() {
        val payload = payload("path/other", "other")
        assertEquals(
            IngestionEventPreprocessingCheck.IGNORED,
            processor.preProcessingCheck(payload)
        )
        assertEquals(
            IngestionEventProcessingResult.IGNORED,
            processor.process(payload, null).result
        )
        assertEquals(
            emptySet(),
            processedPaths
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