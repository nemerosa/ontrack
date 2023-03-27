package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.json.asJson
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractQueueRecordService(
    private val queueRecordStore: QueueRecordStore,
) : QueueRecordService {

    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun start(queuePayload: QueuePayload) {
        queueRecordStore.start(queuePayload)
    }

    override fun setRouting(queuePayload: QueuePayload, routingKey: String) {
        queueRecordStore.save(queuePayload) {
            it.withRoutingKey(routingKey).withState(QueueRecordState.ROUTING_READY)
        }
    }

    override fun sent(queuePayload: QueuePayload) {
        queueRecordStore.save(queuePayload) {
            it.withState(QueueRecordState.SENT)
        }
    }

    override fun received(queuePayload: QueuePayload, queue: String?) {
        queueRecordStore.save(queuePayload) {
            it.withState(QueueRecordState.RECEIVED).withQueue(queue)
        }
    }

    override fun parsed(queuePayload: QueuePayload, payload: Any) {
        queueRecordStore.save(queuePayload) {
            it.withActualPayload(payload.asJson()).withState(QueueRecordState.PARSED)
        }
    }

    override fun processing(queuePayload: QueuePayload) {
        queueRecordStore.save(queuePayload) {
            it.withState(QueueRecordState.PROCESSING)
        }
    }

    override fun completed(queuePayload: QueuePayload) {
        queueRecordStore.save(queuePayload) {
            it.withState(QueueRecordState.COMPLETED)
                .withEndTime(Time.now())
        }
    }

    override fun errored(queuePayload: QueuePayload, exception: Exception) {
        queueRecordStore.save(queuePayload) {
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