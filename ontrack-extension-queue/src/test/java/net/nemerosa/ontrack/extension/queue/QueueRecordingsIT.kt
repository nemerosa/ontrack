package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.extension.queue.record.QueueRecordState
import net.nemerosa.ontrack.extension.queue.record.QueueRecordingsExtension
import net.nemerosa.ontrack.extension.recordings.RecordingsQueryService
import net.nemerosa.ontrack.extension.recordings.RecordingsService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QueueRecordingsIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var recordingsService: RecordingsService

    @Autowired
    private lateinit var recordingsQueryService: RecordingsQueryService

    @Autowired
    private lateinit var queueRecordingsExtension: QueueRecordingsExtension

    @Test
    fun `Queue name is recorded`() {
        val record = QueueTestFixtures.queueRecord(
                state = QueueRecordState.SENT,
                queueName = null,
        )
        asAdmin {
            recordingsService.record(queueRecordingsExtension, record)
            recordingsService.updateRecord(
                    extension = queueRecordingsExtension,
                    id = record.id,
            ) {
                it.withState(QueueRecordState.RECEIVED).withQueue("my-queue")
            }
            val saved = recordingsQueryService.findById(queueRecordingsExtension, record.id)
            assertNotNull(saved) {
                assertEquals(QueueRecordState.RECEIVED, it.state)
                assertEquals("my-queue", it.queueName)
            }
        }
    }

}