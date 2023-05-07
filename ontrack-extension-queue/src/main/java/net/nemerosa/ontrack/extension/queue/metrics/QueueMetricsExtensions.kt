package net.nemerosa.ontrack.extension.queue.metrics

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.metrics.time

fun MeterRegistry.queueMessageSent(queuePayload: QueuePayload) =
    queuePayloadIncrement(QueueMetrics.messageSent, queuePayload)

fun MeterRegistry.queueMessageReceived(queuePayload: QueuePayload) =
    queuePayloadIncrement(QueueMetrics.messageReceived, queuePayload)

fun MeterRegistry.queueProcessCompleted(queuePayload: QueuePayload) =
    queuePayloadIncrement(QueueMetrics.processCompleted, queuePayload)

fun MeterRegistry.queueProcessErrored(queuePayload: QueuePayload) =
    queuePayloadIncrement(QueueMetrics.processErrored, queuePayload)

fun MeterRegistry.queueProcessTime(queuePayload: QueuePayload, code: () -> Unit) {
    time(QueueMetrics.processTime, "processor" to queuePayload.processor) {
        code()
        null
    }
}

private fun MeterRegistry.queuePayloadIncrement(metric: String, queuePayload: QueuePayload) =
    increment(metric, "processor" to queuePayload.processor)
