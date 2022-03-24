package net.nemerosa.ontrack.extension.notifications.dispatching

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueue
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionChannel
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
class DefaultNotificationDispatcher(
    private val notificationChannelRegistry: NotificationChannelRegistry,
    private val notificationQueue: NotificationQueue,
) : NotificationDispatcher {

    override fun dispatchEvent(event: Event, eventSubscription: EventSubscription): NotificationDispatchingResult {
        val potential = eventSubscription.channels.size
        var sent = 0
        var ignored = 0
        eventSubscription.channels.forEach { eventSubscriptionChannel ->
            // Gets the corresponding channel
            val channel = notificationChannelRegistry.findChannel(eventSubscriptionChannel.channel)
            if (channel != null) {
                // Dispatching
                if (dispatchEventToChannel(event, eventSubscription, eventSubscriptionChannel, channel)) {
                    sent++
                } else {
                    ignored++
                }
            }
            // Channel has been ignored
            else {
                ignored++
            }
        }
        return NotificationDispatchingResult(
            potential = potential,
            sent = sent,
            ignored = ignored
        )
    }

    private fun <C> dispatchEventToChannel(
        event: Event,
        eventSubscription: EventSubscription,
        eventSubscriptionChannel: EventSubscriptionChannel,
        channel: NotificationChannel<C>,
    ): Boolean {
        val channelConfig = channel.validate(eventSubscriptionChannel.channelConfig)
        return if (channelConfig.isOk()) {
            val item = Notification(
                channel = eventSubscriptionChannel.channel,
                channelConfig = eventSubscriptionChannel.channelConfig,
                event = event,
            )
            // Publication of the event
            notificationQueue.publish(item)
        } else {
            // Log the channel validation message as an error
            // Not processed
            false
        }
    }

}