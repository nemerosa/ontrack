package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.extension.queue.record.QueueRecord
import net.nemerosa.ontrack.extension.queue.record.QueueRecordState
import net.nemerosa.ontrack.extension.queue.record.QueueRecordingsExtension
import net.nemerosa.ontrack.extension.recordings.RecordingsTestSupport
import org.springframework.stereotype.Component

@Component
class QueueTestSupport(
        private val queueConfigProperties: QueueConfigProperties,
        private val recordingsTestSupport: RecordingsTestSupport,
        private val queueRecordingsExtension: QueueRecordingsExtension,
) {

    fun withSyncQueuing(code: () -> Unit) {
        val old = queueConfigProperties.general.async
        try {
            queueConfigProperties.general.async = false
            code()
        } finally {
            queueConfigProperties.general.async = old
        }
    }

    fun record(
            state: QueueRecordState = QueueRecordState.COMPLETED,
            processor: String = "test",
    ): QueueRecord {
        val record = QueueTestFixtures.queueRecord(
                state = state,
                processor = processor,
        )
        recordingsTestSupport.record(
                queueRecordingsExtension,
                record
        )
        return record
    }

}