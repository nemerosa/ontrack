package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Typed subscription for one given channel.
 *
 * @param name Name of subscription
 * @param channel Channel to send the notifications to
 * @param channelConfig Configuration of the channel
 * @param projectEntity Entity to subscribe to (or null if a global event)
 * @param eventTypes List of event types to subscribe to
 */
fun <C, R> EventSubscriptionService.subscribe(
    name: String,
    channel: NotificationChannel<C, R>,
    channelConfig: C,
    projectEntity: ProjectEntity?,
    keywords: String?,
    origin: String,
    contentTemplate: String?,
    vararg eventTypes: EventType,
) = subscribe(
    EventSubscription(
        name = name,
        channel = channel.type,
        channelConfig = channelConfig.asJson(),
        projectEntity = projectEntity,
        events = eventTypes.map { it.id }.toSet(),
        keywords = keywords,
        disabled = false,
        origin = origin,
        contentTemplate = contentTemplate,
    )
)

/**
 * Gets a required subscription using its name
 *
 * @param projectEntity Entity to look for
 * @param name Name of the subscription
 * @return Subscription
 * @throws EventSubscriptionNameNotFoundException If the name cannot be found
 */
fun EventSubscriptionService.getSubscriptionByName(projectEntity: ProjectEntity?, name: String) =
    findSubscriptionByName(projectEntity, name)
        ?: throw EventSubscriptionNameNotFoundException(projectEntity, name)
