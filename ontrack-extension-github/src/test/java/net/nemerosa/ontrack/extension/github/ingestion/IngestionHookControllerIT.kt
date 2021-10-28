package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull

class IngestionHookControllerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var ingestionHookQueue: IngestionHookQueue

    @Autowired
    private lateinit var ingestionHookPayloadStorage: IngestionHookPayloadStorage

    @Test
    fun `Processing a hook payload`() {
        val controller = IngestionHookController(
            ingestionHookQueue,
            ingestionHookPayloadStorage,
        )

        val body = IngestionHookFixtures.payload()

        controller.hook(body)

        // Checks the payload is stored
        val payloads = ingestionHookPayloadStorage.list()
        assertNotNull(payloads.find { it.payload == body }, "Payload has been stored")

        // TODO Checks the payload has been processed
    }

}