package net.nemerosa.ontrack.extension.queue.record

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.queue.QueuePayload
import java.time.LocalDateTime

data class QueueRecord(
    val state: QueueRecordState,
    val queuePayload: QueuePayload,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val routingKey: String?,
    val queueName: String?,
    val actualPayload: JsonNode?,
    val exception: String?,
    val history: List<QueueRecordHistory>,
) {

    fun withState(state: QueueRecordState) = QueueRecord(
        state = state,
        queuePayload = queuePayload,
        startTime = startTime,
        endTime = endTime,
        routingKey = routingKey,
        queueName = queueName,
        actualPayload = actualPayload,
        exception = exception,
        history = listOf(
            QueueRecordHistory(
                state = state,
                time = Time.now()
            )
        ) + history,
    )

    fun withRoutingKey(routingKey: String) = QueueRecord(
        state = state,
        queuePayload = queuePayload,
        startTime = startTime,
        endTime = endTime,
        routingKey = routingKey,
        queueName = queueName,
        actualPayload = actualPayload,
        exception = exception,
        history = history,
    )

    fun withQueue(queueName: String?) = QueueRecord(
        state = state,
        queuePayload = queuePayload,
        startTime = startTime,
        endTime = endTime,
        routingKey = routingKey,
        queueName = queueName,
        actualPayload = actualPayload,
        exception = exception,
        history = history,
    )

    fun withActualPayload(actualPayload: JsonNode) = QueueRecord(
        state = state,
        queuePayload = queuePayload,
        startTime = startTime,
        endTime = endTime,
        routingKey = routingKey,
        queueName = queueName,
        actualPayload = actualPayload,
        exception = exception,
        history = history,
    )

    fun withException(exception: String) = QueueRecord(
        state = state,
        queuePayload = queuePayload,
        startTime = startTime,
        endTime = endTime,
        routingKey = routingKey,
        queueName = queueName,
        actualPayload = actualPayload,
        exception = exception,
        history = history,
    )

    fun withEndTime(endTime: LocalDateTime) = QueueRecord(
        state = state,
        queuePayload = queuePayload,
        startTime = startTime,
        endTime = endTime,
        routingKey = routingKey,
        queueName = queueName,
        actualPayload = actualPayload,
        exception = exception,
        history = history,
    )

    companion object {
        fun create(queuePayload: QueuePayload): QueueRecord {
            val time = Time.now()
            return QueueRecord(
                state = QueueRecordState.STARTED,
                queuePayload = queuePayload,
                startTime = time,
                endTime = null,
                routingKey = null,
                queueName = null,
                actualPayload = null,
                exception = null,
                history = listOf(
                    QueueRecordHistory(
                        state = QueueRecordState.STARTED,
                        time = time,
                    )
                )
            )
        }
    }
}