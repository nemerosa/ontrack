package net.nemerosa.ontrack.extension.queue.record

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.extension.recordings.Recording
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIIgnore
import java.time.LocalDateTime

@APIDescription("Recording a queuing event.")
data class QueueRecord(
    @APIDescription("State of the queuing")
    val state: QueueRecordState,
    @APIDescription("Payload for the queue message")
    val queuePayload: QueuePayload,
    @APIDescription("Time of creation")
    @APIIgnore("Managed by the top recordings record")
    override val startTime: LocalDateTime,
    @APIDescription("End of processing")
    @APIIgnore("Managed by the top recordings record")
    override val endTime: LocalDateTime?,
    @APIDescription("Queue routing key being used")
    val routingKey: String?,
    @APIDescription("Queue where the message was stored")
    val queueName: String?,
    @APIDescription("Actual payload sent to the processing")
    val actualPayload: JsonNode?,
    @APIDescription("Error on processing")
    val exception: String?,
    @APIDescription("History of the states")
    val history: List<QueueRecordHistory>,
): Recording {

    override val id: String = queuePayload.id

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

    fun withStartTime(startTime: LocalDateTime) = QueueRecord(
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