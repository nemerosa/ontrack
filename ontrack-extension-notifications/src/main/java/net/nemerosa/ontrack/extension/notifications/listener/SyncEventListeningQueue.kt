package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.model.events.Event
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.notifications.dispatching.queue",
    name = ["async"],
    havingValue = "false",
    matchIfMissing = false,
)
class SyncEventListeningQueue(
    private val eventListeningService: EventListeningService,
) : EventListeningQueue {

    override fun publish(event: Event) {
        eventListeningService.onEvent(event)
    }

}