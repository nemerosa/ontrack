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

        val body = IngestionHookFixtures.payload

        controller.hook(body)

        assertEquals(body, storedPayload?.payload)
        assertEquals(body, queuedPayload?.payload)
    }

}