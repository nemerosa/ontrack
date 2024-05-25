package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.events.EventListenerService
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class EventListenerServiceImpl(
    private val context: ApplicationContext,
) : EventListenerService {

    private val listeners: Collection<EventListener> by lazy {
        context.getBeansOfType(
            EventListener::class.java
        ).values
    }

    override fun onEvent(event: Event) {
        listeners.forEach { it.onEvent(event) }
    }
}
