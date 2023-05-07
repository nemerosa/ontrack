package net.nemerosa.ontrack.extension.recordings.store

import net.nemerosa.ontrack.extension.recordings.RecordingsTestFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RecordingsStoreIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var recordingsStore: RecordingsStore

    @Test
    fun `Saving and retrieving a record`() {
        val record = RecordingsTestFixtures.sampleRecord()
        recordingsStore.save(
                RecordingsTestFixtures.testStore,
                record
        )
        val saved = recordingsStore.findById(RecordingsTestFixtures.testStore, record.id)
        assertNotNull(saved, "Record retrieved") {
            assertEquals(record, saved)
        }
    }

    @Test
    fun `Pagination filter`() {
        val messages = (1..3).map { uid("msg_") }
        messages.forEach { message ->
            repeat(10) {
                val record = RecordingsTestFixtures.sampleRecord(message = message)
                recordingsStore.save(RecordingsTestFixtures.testStore, record)
            }
        }

        val totalCount = recordingsStore.countByFilter(
                store = RecordingsTestFixtures.testStore,
                queries = listOf("data->'data'->>'message' = :message"),
                queryVariables = mapOf("message" to messages[1]),
        )
        assertEquals(10, totalCount)

        val page = recordingsStore.findByFilter(
                store = RecordingsTestFixtures.testStore,
                queries = listOf("data->'data'->>'message' = :message"),
                queryVariables = mapOf("message" to messages[1]),
                offset = 0,
                size = 5,
        )
        assertEquals(10, page.pageInfo.totalSize)
        assertEquals(5, page.pageInfo.currentSize)
        assertEquals(5, page.pageItems.size)
        assertTrue(page.pageItems.all { it.data.path("message").asText() == messages[1] })
    }

}