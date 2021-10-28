package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
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
            MockIngestionHookSignatureService(),
        )

        val body = IngestionHookFixtures.payloadBody().format()
        val headers = IngestionHookFixtures.payloadHeaders()

        controller.hook(
            body = body,
            gitHubDelivery = headers.gitHubDelivery,
            gitHubEvent = headers.gitHubEvent,
            gitHubHookID = headers.gitHubHookID,
            gitHubHookInstallationTargetID = headers.gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = headers.gitHubHookInstallationTargetType,
            signature = "",
        )

        // Checks the payload is stored
        val payloads = ingestionHookPayloadStorage.list()
        assertNotNull(payloads.find { it.payload == body.asJson() }, "Payload has been stored")

        // TODO Checks the payload has been processed
    }

}