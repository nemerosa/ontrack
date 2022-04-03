package net.nemerosa.ontrack.extension.notifications.metrics

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.metrics.increment

fun MeterRegistry.incrementForEvent(
    name: String,
    event: Event,
) = increment(
    name,
    *getTags(event)
)

private fun getTags(event: Event): Array<Pair<String, String>> =
    arrayOf(
        "type" to event.eventType.id
    )
