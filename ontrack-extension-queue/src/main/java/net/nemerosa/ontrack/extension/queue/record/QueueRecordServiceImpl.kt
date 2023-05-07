package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.extension.recordings.RecordingsService
import net.nemerosa.ontrack.json.asJson
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QueueRecordServiceImpl(
        private val queueRecordingsExtension: QueueRecordingsExtension,
        private val recordingsService: RecordingsService,
) : QueueRecordService {

    override fun start(queuePayload: QueuePayload) {
        val record = QueueRecord.create(queuePayload)
        recordingsService.record(queueRecordingsExtension, record)
    }

    override fun setRouting(queuePayload: QueuePayload, routingKey: String) {
        recordingsService.updateRecord(queueRecordingsExtension, queuePayload.id) {
            it.withRoutingKey(routingKey).withState(QueueRecordState.ROUTING_READY)
        }
    }

    override fun sent(queuePayload: QueuePayload) {
        recordingsService.updateRecord(queueRecordingsExtension, queuePayload.id) {
            it.withState(QueueRecordState.SENT)
        }
    }

    override fun received(queuePayload: QueuePayload, queue: String?) {
        recordingsService.updateRecord(queueRecordingsExtension, queuePayload.id) {
            it.withState(QueueRecordState.RECEIVED).withQueue(queue)
        }
    }

    override fun parsed(queuePayload: QueuePayload, payload: Any) {
        recordingsService.updateRecord(queueRecordingsExtension, queuePayload.id) {
            it.withActualPayload(payload.asJson()).withState(QueueRecordState.PARSED)
        }
    }

    override fun cancelled(queuePayload: QueuePayload, cancelReason: String) {
        recordingsService.updateRecord(queueRecordingsExtension, queuePayload.id) {
            it.withException(cancelReason).withState(QueueRecordState.CANCELLED)
        }
    }

    override fun processing(queuePayload: QueuePayload) {
        recordingsService.updateRecord(queueRecordingsExtension, queuePayload.id) {
            it.withState(QueueRecordState.PROCESSING)
        }
    }

    override fun completed(queuePayload: QueuePayload) {
        recordingsService.updateRecord(queueRecordingsExtension, queuePayload.id) {
            it.withState(QueueRecordState.COMPLETED)
                    .withEndTime(Time.now())
        }
    }

    override fun errored(queuePayload: QueuePayload, exception: Exception) {
        recordingsService.updateRecord(queueRecordingsExtension, queuePayload.id) {
            it.withState(QueueRecordState.ERRORED)
                    .withEndTime(Time.now())
                    .withException(reducedStackTrace(exception))
        }
    }

    companion object {
        private const val MAX_STACK_HEIGHT = 20

        fun reducedStackTrace(error: Throwable) =
                ExceptionUtils.getStackFrames(error).take(MAX_STACK_HEIGHT).joinToString("\n")
    }

}