package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Typed subscription for one given channel.
 *
 * @param channel Channel to send the notifications to
 * @param channelConfig Configuration of the channel
 * @param projectEntity Entity to subscribe to (or null if a global event)
 * @param eventTypes List of event types to subscribe to
 */
fun <C> EventSubscriptionService.subscribe(
    channel: NotificationChannel<C>,
    channelConfig: C,
    projectEntity: ProjectEntity,
    vararg eventTypes: EventType,
) = subscribe(
    EventSubscription(
        channels = setOf(
            EventSubscriptionChannel(
                channel = channel.type,
                channelConfig = channelConfig.asJson(),
            ),
        ),
        projectEntity = projectEntity,
        events = eventTypes.map { it.id }.toSet()
    )
)
