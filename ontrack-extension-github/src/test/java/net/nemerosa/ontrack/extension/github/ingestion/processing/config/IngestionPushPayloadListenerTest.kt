package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerOutcome
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class IngestionPushPayloadListenerTest {

    private lateinit var configService: ConfigService
    private lateinit var listener: IngestionPushPayloadListener

    @Before
    fun before() {
        configService = mockk(relaxed = true)
        listener = IngestionPushPayloadListener(configService)
    }

    @Test
    fun `Processing when config is added`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            added = listOf(IngestionPushPayloadListener.CONFIG_FILE_PATH)
        )
        assertEquals(
            PushPayloadListenerOutcome.PROCESSED,
            listener.process(payload)
        )
        verify {
            configService.saveConfig(
                repository = IngestionHookFixtures.sampleRepository(),
                branch = IngestionHookFixtures.sampleBranch,
                path = IngestionPushPayloadListener.CONFIG_FILE_PATH,
            )
        }
    }

    @Test
    fun `Processing when config is modified`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            modified = listOf(IngestionPushPayloadListener.CONFIG_FILE_PATH)
        )
        assertEquals(
            PushPayloadListenerOutcome.PROCESSED,
            listener.process(payload)
        )
        verify {
            configService.saveConfig(
                repository = IngestionHookFixtures.sampleRepository(),
                branch = IngestionHookFixtures.sampleBranch,
                path = IngestionPushPayloadListener.CONFIG_FILE_PATH,
            )
        }
    }

    @Test
    fun `Processing when config is deleted`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            removed = listOf(IngestionPushPayloadListener.CONFIG_FILE_PATH)
        )
        assertEquals(
            PushPayloadListenerOutcome.PROCESSED,
            listener.process(payload)
        )
        verify {
            configService.removeConfig(
                repository = IngestionHookFixtures.sampleRepository(),
                branch = IngestionHookFixtures.sampleBranch,
            )
        }
    }

    @Test
    fun `Ignoring when path is not included at all`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            added = listOf("any/other/path"),
            modified = listOf("any/other/path"),
            removed = listOf("any/other/path"),
        )
        assertEquals(
            PushPayloadListenerOutcome.IGNORED,
            listener.process(payload)
        )
        verify {
            configService wasNot Called
        }
    }

}