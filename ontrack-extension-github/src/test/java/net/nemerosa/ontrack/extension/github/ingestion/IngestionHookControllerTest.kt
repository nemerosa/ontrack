package net.nemerosa.ontrack.extension.github.ingestion

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.security.SecurityService
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IngestionHookControllerTest {

    @Test
    fun `Storage and queuing`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val securityService = MockSecurityService()
        val controller = IngestionHookController(queue, storage, MockIngestionHookSignatureService(), securityService)

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
    fun `No storage nor queuing for a ping`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val securityService = MockSecurityService()
        val controller = IngestionHookController(queue, storage, MockIngestionHookSignatureService(), securityService)

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