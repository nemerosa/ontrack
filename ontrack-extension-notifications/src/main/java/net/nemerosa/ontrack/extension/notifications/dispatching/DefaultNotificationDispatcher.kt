package net.nemerosa.ontrack.extension.notifications.dispatching

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueue
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
class DefaultNotificationDispatcher(
    private val notificationChannelRegistry: NotificationChannelRegistry,
    private val notificationQueue: NotificationQueue,
) : NotificationDispatcher {

    override fun dispatchEvent(event: Event, eventSubscription: EventSubscription): NotificationDispatchingResult {
        // Gets the corresponding channel
        val channel = notificationChannelRegistry.findChannel(eventSubscription.channel)
        return if (channel != null && channel.enabled) {
            // Dispatching
            if (dispatchEventToChannel(event, eventSubscription, channel)) {
                NotificationDispatchingResult.SENT
            } else {
                NotificationDispatchingResult.IGNORED
            }
        }
        // Channel has been ignored
        else {
            NotificationDispatchingResult.IGNORED
        }
    }

    private fun <C> dispatchEventToChannel(
        event: Event,
        eventSubscription: EventSubscription,
        channel: NotificationChannel<C>,
    ): Boolean {
        val channelConfig = channel.validate(eventSubscription.channelConfig)
        return if (channelConfig.isOk()) {
            val item = Notification(
                channel = eventSubscription.channel,
                channelConfig = eventSubscription.channelConfig,
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