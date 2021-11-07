package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DefaultIngestionHookPayloadStorageIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var storage: IngestionHookPayloadStorage

    @Test
    fun `Storing and retrieving`() {
        val payload = IngestionHookFixtures.sampleWorkflowRunIngestionPayload()
        asAdmin {
            storage.store(payload)
            // Get it back
            assertTrue(storage.count() >= 1, "At least one item stored")
            assertNotNull(storage.list().find { it.uuid == payload.uuid }, "Item has been stored")
        }
    }

}