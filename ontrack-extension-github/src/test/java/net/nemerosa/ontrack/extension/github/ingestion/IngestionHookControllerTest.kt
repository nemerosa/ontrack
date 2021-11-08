package net.nemerosa.ontrack.extension.github.ingestion

import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.payload.GitHubIngestionHookSignatureMismatchException
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookSignatureCheckResult
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettingsMissingTokenException
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.json.parseAsJson
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
        val meterRegistry = mockk<MeterRegistry>()
        val controller =
            IngestionHookController(queue, storage, MockIngestionHookSignatureService(), securityService, meterRegistry)

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
    fun `Missing token for the signature`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val securityService = MockSecurityService()
        val meterRegistry = mockk<MeterRegistry>()
        val controller =
            IngestionHookController(
                queue,
                storage,
                MockIngestionHookSignatureService(IngestionHookSignatureCheckResult.MISSING_TOKEN),
                securityService,
                meterRegistry
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
        val controller =
            IngestionHookController(
                queue,
                storage,
                MockIngestionHookSignatureService(IngestionHookSignatureCheckResult.MISMATCH),
                securityService,
                meterRegistry
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
        val meterRegistry = mockk<MeterRegistry>()
        val controller =
            IngestionHookController(queue, storage, MockIngestionHookSignatureService(), securityService, meterRegistry)

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

}