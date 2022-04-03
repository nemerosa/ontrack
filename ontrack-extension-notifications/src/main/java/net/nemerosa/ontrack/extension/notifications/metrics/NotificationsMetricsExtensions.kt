package net.nemerosa.ontrack.extension.notifications.metrics

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.notifications.dispatching.NotificationDispatchingResult
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.metrics.increment

fun MeterRegistry.incrementForEvent(
    name: String,
    event: Event,
) = increment(
    name,
    *getTags(event)
)

fun MeterRegistry.incrementForDispatching(
    name: String,
    event: Event,
    subscription: EventSubscription,
    result: NotificationDispatchingResult? = null,
) = increment(
    name,
    *getTags(event, subscription, result)
)

fun getTags(
    event: Event,
    subscription: EventSubscription,
    result: NotificationDispatchingResult?,
): Array<Pair<String, String>> = arrayOf(
    "type" to event.eventType.id,
    "channel" to subscription.channel,
) + if (result != null) {
    arrayOf("result" to result.name)
} else {
    emptyArray()
}

private fun getTags(event: Event): Array<Pair<String, String>> =
    arrayOf(
        "type" to event.eventType.id
    )
