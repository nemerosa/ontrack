package net.nemerosa.ontrack.extension.notifications.dispatching

import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
class DefaultNotificationDispatcher : NotificationDispatcher {

    override fun dispatchEvent(event: Event, eventSubscription: EventSubscription): NotificationDispatchingResult {
        TODO("Not yet implemented")
    }

}