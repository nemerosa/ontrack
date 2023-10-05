package net.nemerosa.ontrack.extension.github.autoversioning

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.github.ingestion.FileLoaderService
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
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

    @Test
    fun `Precheck with added config file is processed`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            added = listOf(AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        )
        val check = listener.preProcessCheck(payload)
        assertEquals(PushPayloadListenerCheck.TO_BE_PROCESSED, check)
    }

    @Test
    fun `Precheck with modified config file is processed`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            modified = listOf(AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        )
        val check = listener.preProcessCheck(payload)
        assertEquals(PushPayloadListenerCheck.TO_BE_PROCESSED, check)
    }

    @Test
    fun `Precheck with removed config file is processed`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            removed = listOf(AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        )
        val check = listener.preProcessCheck(payload)
        assertEquals(PushPayloadListenerCheck.TO_BE_PROCESSED, check)
    }

    @Test
    fun `Precheck with no config file is not processed`() {
        val payload = IngestionHookFixtures.samplePushPayload()
        val check = listener.preProcessCheck(payload)
        assertEquals(PushPayloadListenerCheck.IGNORED, check)
    }

    @Test
    fun `Added config file is processed`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            added = listOf(AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        )
        validContent()
        val details = listener.process(payload, null)
        assertEquals(IngestionEventProcessingResult.PROCESSED, details.result)
        verify {
            autoVersioningConfigurationService.setupAutoVersioning(any(), config)
        }
    }

    @Test
    fun `Added config file without content is processed`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            added = listOf(AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        )
        noValidContent()
        val details = listener.process(payload, null)
        assertEquals(IngestionEventProcessingResult.PROCESSED, details.result)
        verify {
            autoVersioningConfigurationService.setupAutoVersioning(any(), null)
        }
    }

    @Test
    fun `Modified config file is processed`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            modified = listOf(AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        )
        validContent()
        val details = listener.process(payload, null)
        assertEquals(IngestionEventProcessingResult.PROCESSED, details.result)
        verify {
            autoVersioningConfigurationService.setupAutoVersioning(any(), config)
        }
    }

    @Test
    fun `Deleted config file is processed`() {
        val payload = IngestionHookFixtures.samplePushPayload(
            removed = listOf(AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        )
        val details = listener.process(payload, null)
        assertEquals(IngestionEventProcessingResult.PROCESSED, details.result)
        verify {
            autoVersioningConfigurationService.setupAutoVersioning(any(), null)
        }
    }

    @Test
    fun `Unchanged config file is not processed`() {
        val payload = IngestionHookFixtures.samplePushPayload()
        val details = listener.process(payload, null)
        assertEquals(IngestionEventProcessingResult.IGNORED, details.result)
        verify(exactly = 0) {
            autoVersioningConfigurationService.setupAutoVersioning(any(), any())
        }
    }

    private fun validContent() {
        every {
            fileLoaderService.loadFile(any(), AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        } returns content
    }

    private fun noValidContent() {
        every {
            fileLoaderService.loadFile(any(), AutoVersioningConfigPushPayloadListener.AUTO_VERSIONING_CONFIG_FILE_PATH)
        } returns null
    }

    companion object {

        private val content = """
            configurations:
                - project: source
                  branch: "release/.*"
                  promotion: GOLD
                  path: gradle.properties
                  property: dep-version
                  auto-approval: true
                  auto-approval-mode: CLIENT
        """.trimIndent()

        private val config = AutoVersioningConfig(
            configurations = listOf(
                AutoVersioningSourceConfig(
                    sourceProject = "source",
                    sourceBranch = "release/.*",
                    sourcePromotion = "GOLD",
                    targetPath = "gradle.properties",
                    targetProperty = "dep-version",
                    autoApproval = true,
                    autoApprovalMode = AutoApprovalMode.CLIENT,
                    qualifier = null,
                    reviewers = null,
                )
            )
        )

    }

}