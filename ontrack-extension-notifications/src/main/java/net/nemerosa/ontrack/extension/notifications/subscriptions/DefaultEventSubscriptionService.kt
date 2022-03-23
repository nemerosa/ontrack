package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Service

@Service
class DefaultEventSubscriptionService : EventSubscriptionService {

    override fun subscribe(subscription: EventSubscription): SavedEventSubscription {
        TODO("Not yet implemented")
    }

    override fun forEveryMatchingSubscription(event: Event, code: (subscription: EventSubscription) -> Unit) {
        TODO("Not yet implemented")
    }

}