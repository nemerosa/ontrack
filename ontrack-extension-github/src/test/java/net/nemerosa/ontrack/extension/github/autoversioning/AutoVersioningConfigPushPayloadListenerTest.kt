package net.nemerosa.ontrack.extension.github.autoversioning

import io.mockk.mockk
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.github.ingestion.FileLoaderService
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerCheck
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AutoVersioningConfigPushPayloadListenerTest {

    private lateinit var ingestionModelAccessService: IngestionModelAccessService
    private lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService
    private lateinit var fileLoaderService: FileLoaderService

    private lateinit var listener: AutoVersioningConfigPushPayloadListener

    @BeforeEach
    fun init() {
        ingestionModelAccessService = mockk(relaxed = true)
        autoVersioningConfigurationService = mockk(relaxed = true)
        fileLoaderService = mockk(relaxed = true)
        listener = AutoVersioningConfigPushPayloadListener(
            ingestionModelAccessService,
            autoVersioningConfigurationService,
            fileLoaderService,
        )
    }

    @Test
    fun `Precheck with tag is not processed`() {
        val payload = IngestionHookFixtures.samplePushPayload(ref = "refs/tags/0.1.0")
        val check = listener.preProcessCheck(payload)
        assertEquals(PushPayloadListenerCheck.IGNORED, check)
    }

}