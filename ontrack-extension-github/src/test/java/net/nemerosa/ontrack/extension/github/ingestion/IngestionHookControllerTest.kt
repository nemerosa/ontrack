package net.nemerosa.ontrack.extension.github.ingestion

import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.payload.GitHubIngestionHookSignatureMismatchException
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookSignatureCheckResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettingsMissingTokenException
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class IngestionHookControllerTest {

    @Test
    fun `Storage and queuing`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val securityService = MockSecurityService()
        val meterRegistry = mockk<MeterRegistry>(relaxed = true)

        val ingestionEventProcessor = mockIngestionEventProcessor()
        every { ingestionEventProcessor.preProcessingCheck(any()) } returns IngestionEventPreprocessingCheck.TO_BE_PROCESSED

        val controller =
            IngestionHookController(
                queue,
                storage,
                MockIngestionHookSignatureService(),
                securityService,
                meterRegistry,
                mockSettings(),
                listOf(ingestionEventProcessor),
            )

        var storedPayload: IngestionHookPayload? = null
        var queuedPayload: IngestionHookPayload? = null

        every { storage.store(any()) } answers {
            storedPayload = this.arg(0)
        }

        every { queue.queue(any()) } answers {
            queuedPayload = this.arg(0)
        }

        val body = IngestionHookFixtures.sampleWorkflowRunJsonPayload().format()
        val headers = IngestionHookFixtures.payloadHeaders(event = "workflow_run")

        controller.hook(
            body = body,
            gitHubDelivery = headers.gitHubDelivery,
            gitHubEvent = headers.gitHubEvent,
            gitHubHookID = headers.gitHubHookID,
            gitHubHookInstallationTargetID = headers.gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = headers.gitHubHookInstallationTargetType,
            signature = "",
        )

        assertEquals(body.parseAsJson(), storedPayload?.payload)
        assertEquals(body.parseAsJson(), queuedPayload?.payload)
    }

    @Test
    fun `Excluding a repository`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val securityService = MockSecurityService()
        val meterRegistry = mockk<MeterRegistry>(relaxed = true)

        val ingestionEventProcessor = mockIngestionEventProcessor()
        every { ingestionEventProcessor.preProcessingCheck(any()) } returns IngestionEventPreprocessingCheck.TO_BE_PROCESSED

        val controller =
            IngestionHookController(
                queue,
                storage,
                MockIngestionHookSignatureService(),
                securityService,
                meterRegistry,
                mockSettings(
                    repositoryExcludes = IngestionHookFixtures.sampleRepository,
                ),
                listOf(ingestionEventProcessor),
            )

        var storedPayload: IngestionHookPayload? = null
        var queuedPayload: IngestionHookPayload? = null

        every { storage.store(any()) } answers {
            storedPayload = this.arg(0)
        }

        every { queue.queue(any()) } answers {
            queuedPayload = this.arg(0)
        }

        val body = IngestionHookFixtures.sampleWorkflowRunJsonPayload().format()
        val headers = IngestionHookFixtures.payloadHeaders(event = "workflow_run")

        controller.hook(
            body = body,
            gitHubDelivery = headers.gitHubDelivery,
            gitHubEvent = headers.gitHubEvent,
            gitHubHookID = headers.gitHubHookID,
            gitHubHookInstallationTargetID = headers.gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = headers.gitHubHookInstallationTargetType,
            signature = "",
        )

        assertNull(storedPayload?.payload)
        assertNull(queuedPayload?.payload)
    }

    @Test
    fun `Missing token for the signature`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val securityService = MockSecurityService()
        val meterRegistry = mockk<MeterRegistry>()
        val ingestionEventProcessor = mockIngestionEventProcessor()
        val controller =
            IngestionHookController(
                queue,
                storage,
                MockIngestionHookSignatureService(IngestionHookSignatureCheckResult.MISSING_TOKEN),
                securityService,
                meterRegistry,
                mockSettings(),
                listOf(ingestionEventProcessor),
            )

        val body = IngestionHookFixtures.sampleWorkflowRunJsonPayload().format()
        val headers = IngestionHookFixtures.payloadHeaders(event = "workflow_run")

        assertFailsWith<GitHubIngestionSettingsMissingTokenException> {
            controller.hook(
                body = body,
                gitHubDelivery = headers.gitHubDelivery,
                gitHubEvent = headers.gitHubEvent,
                gitHubHookID = headers.gitHubHookID,
                gitHubHookInstallationTargetID = headers.gitHubHookInstallationTargetID,
                gitHubHookInstallationTargetType = headers.gitHubHookInstallationTargetType,
                signature = "",
            )
        }
    }

    @Test
    fun `Wrong signature`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val securityService = MockSecurityService()
        val meterRegistry = mockk<MeterRegistry>(relaxed = true)
        val ingestionEventProcessor = mockIngestionEventProcessor()
        val controller =
            IngestionHookController(
                queue,
                storage,
                MockIngestionHookSignatureService(IngestionHookSignatureCheckResult.MISMATCH),
                securityService,
                meterRegistry,
                mockSettings(),
                listOf(ingestionEventProcessor),
            )

        val body = IngestionHookFixtures.sampleWorkflowRunJsonPayload().format()
        val headers = IngestionHookFixtures.payloadHeaders(event = "workflow_run")

        assertFailsWith<GitHubIngestionHookSignatureMismatchException> {
            controller.hook(
                body = body,
                gitHubDelivery = headers.gitHubDelivery,
                gitHubEvent = headers.gitHubEvent,
                gitHubHookID = headers.gitHubHookID,
                gitHubHookInstallationTargetID = headers.gitHubHookInstallationTargetID,
                gitHubHookInstallationTargetType = headers.gitHubHookInstallationTargetType,
                signature = "",
            )
        }
    }

    @Test
    fun `No storage nor queuing for a ping`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val securityService = MockSecurityService()
        val meterRegistry = mockk<MeterRegistry>(relaxed = true)
        val ingestionEventProcessor = mockIngestionEventProcessor("ping")
        every { ingestionEventProcessor.preProcessingCheck(any()) } returns IngestionEventPreprocessingCheck.IGNORED

        val controller =
            IngestionHookController(
                queue,
                storage,
                MockIngestionHookSignatureService(),
                securityService,
                meterRegistry,
                mockSettings(),
                listOf(ingestionEventProcessor)
            )

        var storedPayload: IngestionHookPayload? = null
        var queuedPayload: IngestionHookPayload? = null

        every { storage.store(any()) } answers {
            storedPayload = this.arg(0)
        }

        every { queue.queue(any()) } answers {
            queuedPayload = this.arg(0)
        }

        val body = IngestionHookFixtures.sampleWorkflowRunJsonPayload().format()
        val headers = IngestionHookFixtures.payloadHeaders(event = "ping")

        controller.hook(
            body = body,
            gitHubDelivery = headers.gitHubDelivery,
            gitHubEvent = headers.gitHubEvent,
            gitHubHookID = headers.gitHubHookID,
            gitHubHookInstallationTargetID = headers.gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = headers.gitHubHookInstallationTargetType,
            signature = "",
        )

        assertNull(storedPayload?.payload)
        assertNull(queuedPayload?.payload)
    }

    private fun mockIngestionEventProcessor(event: String = "workflow_run"): IngestionEventProcessor {
        val ingestionEventProcessor = mockk<IngestionEventProcessor>()
        every { ingestionEventProcessor.event } returns event
        return ingestionEventProcessor
    }

    private fun mockSettings(
        repositoryIncludes: String = GitHubIngestionSettings.DEFAULT_REPOSITORY_INCLUDES,
        repositoryExcludes: String = GitHubIngestionSettings.DEFAULT_REPOSITORY_EXCLUDES,
    ): CachedSettingsService {
        val cachedSettingsService = mockk<CachedSettingsService>()
        val settings = GitHubIngestionSettings(
            token = "token",
            repositoryIncludes = repositoryIncludes,
            repositoryExcludes = repositoryExcludes,
        )
        every { cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java) } returns settings
        return cachedSettingsService
    }

}