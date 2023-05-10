package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.queue.record.QueueRecord
import net.nemerosa.ontrack.extension.queue.record.QueueRecordHistory
import net.nemerosa.ontrack.extension.queue.record.QueueRecordState
import net.nemerosa.ontrack.extension.queue.source.createQueueSource
import net.nemerosa.ontrack.json.asJson
import java.util.*

object QueueTestFixtures {

    fun queueRecord(
            state: QueueRecordState = QueueRecordState.COMPLETED,
            processor: String = "test",
            queueName: String? = "queue",
    ): QueueRecord {
        val id = UUID.randomUUID().toString()
        val payload = mapOf("message" to "Sample message").asJson()
        val time = Time.now()
        return QueueRecord(
                state = state,
                queuePayload = QueuePayload(
                        id = id,
                        processor = processor,
                        body = payload,
                ),
                startTime = time,
                endTime = time,
                routingKey = "routing",
                queueName = queueName,
                actualPayload = payload,
                exception = null,
                history = listOf(
                        QueueRecordHistory(
                                state = QueueRecordState.COMPLETED,
                                time = time,
                        )
                ),
                source = TestQueueSourceExtension.instance.createQueueSource("")
        )
    }

}