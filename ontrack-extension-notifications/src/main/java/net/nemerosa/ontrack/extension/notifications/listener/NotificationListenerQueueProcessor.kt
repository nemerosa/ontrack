package net.nemerosa.ontrack.extension.notifications.listener

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics
import net.nemerosa.ontrack.extension.notifications.metrics.incrementForEvent
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.model.events.SerializableEventService
import net.nemerosa.ontrack.model.metrics.increment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class NotificationListenerQueueProcessor(
    private val meterRegistry: MeterRegistry,
    private val serializableEventService: SerializableEventService,
    private val eventListeningService: EventListeningService,
) : QueueProcessor<NotificationListenerQueuePayload> {

    private val logger: Logger = LoggerFactory.getLogger(NotificationListenerQueueProcessor::class.java)

    override val id: String = "notification.listener"
    override val payloadType: KClass<NotificationListenerQueuePayload> = NotificationListenerQueuePayload::class

    override fun isCancelled(payload: NotificationListenerQueuePayload): String? = null

    override fun process(payload: NotificationListenerQueuePayload) {
        try {
            val event = serializableEventService.hydrate(payload.serializedEvent)
            meterRegistry.incrementForEvent(
                NotificationsMetrics.event_listening_dequeued,
                event
            )
            eventListeningService.onEvent(event)
        } catch (any: Throwable) {
            meterRegistry.increment(
                NotificationsMetrics.event_listening_dequeued_error
            )
            logger.error(
                "Uncaught exception during notification event dispatching",
                any
            )
        }
    }

    override fun getRoutingIdentifier(payload: NotificationListenerQueuePayload): String =
        payload.serializedEvent.id.toString()
}