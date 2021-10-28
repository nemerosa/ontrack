package net.nemerosa.ontrack.extension.github.ingestion

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import org.junit.Test
import kotlin.test.assertEquals

class IngestionHookControllerTest {

    @Test
    fun `Storage and queuing`() {
        val storage = mockk<IngestionHookPayloadStorage>()
        val queue = mockk<IngestionHookQueue>()
        val controller = IngestionHookController(queue, storage)

        var storedPayload: IngestionHookPayload? = null
        var queuedPayload: IngestionHookPayload? = null

        every { storage.store(any()) } answers {
            storedPayload = this.arg(0)
        }

        every { queue.queue(any()) } answers {
            queuedPayload = this.arg(0)
        }

        val body = IngestionHookFixtures.payloadBody()
        val headers = IngestionHookFixtures.payloadHeaders()

        controller.hook(
            body = body,
            gitHubDelivery = headers.gitHubDelivery,
            gitHubEvent = headers.gitHubEvent,
            gitHubHookID = headers.gitHubHookID,
            gitHubHookInstallationTargetID = headers.gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = headers.gitHubHookInstallationTargetType
        )

        assertEquals(body, storedPayload?.payload)
        assertEquals(body, queuedPayload?.payload)
    }

}