package net.nemerosa.ontrack.service.events

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.common.logTime
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.events.EventListenerService
import net.nemerosa.ontrack.model.metrics.measure
import net.nemerosa.ontrack.model.metrics.time
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class EventListenerServiceImpl(
    private val context: ApplicationContext,
    private val meterRegistry: MeterRegistry,
) : EventListenerService {

    private val logger = LoggerFactory.getLogger(EventListenerServiceImpl::class.java)

    private val listeners: Collection<EventListener> by lazy {
        context.getBeansOfType(
            EventListener::class.java
        ).values
    }

    override fun onEvent(event: Event) {
        listeners.forEach {
            onEventForListener(it, event)
        }
    }

    private fun onEventForListener(eventListener: EventListener, event: Event) {
        meterRegistry.time(
            name = EventListenerMetrics.METRIC_ONTRACK_EVENT_LISTENER_TIME,
            EventListenerMetrics.TAG_EVENT_LISTENER to eventListener::class.java.name,
        ) {
            logger.logTime("Event listener ${eventListener.javaClass.simpleName}") {
                eventListener.onEvent(event)
            }
        }
    }
}
