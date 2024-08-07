package net.nemerosa.ontrack.extension.notifications.dispatching

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.model.NotificationSourceData
import net.nemerosa.ontrack.extension.notifications.model.createData
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueue
import net.nemerosa.ontrack.extension.notifications.sources.EntitySubscriptionNotificationSource
import net.nemerosa.ontrack.extension.notifications.sources.EntitySubscriptionNotificationSourceDataType
import net.nemerosa.ontrack.extension.notifications.sources.GlobalSubscriptionNotificationSource
import net.nemerosa.ontrack.extension.notifications.sources.GlobalSubscriptionNotificationSourceDataType
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.stereotype.Component

@Component
class DefaultNotificationDispatcher(
    private val notificationChannelRegistry: NotificationChannelRegistry,
    private val notificationQueue: NotificationQueue,
    private val applicationLogService: ApplicationLogService,
    private val entitySubscriptionNotificationSource: EntitySubscriptionNotificationSource,
    private val globalSubscriptionNotificationSource: GlobalSubscriptionNotificationSource,
) : NotificationDispatcher {

    override fun dispatchEvent(event: Event, eventSubscription: EventSubscription): NotificationDispatchingResult {
        // If the subscription is disabled, not doing anything
        if (eventSubscription.disabled) {
            return NotificationDispatchingResult.SUBSCRIPTION_DISABLED
        }
        // Gets the corresponding channel
        val channel = notificationChannelRegistry.findChannel(eventSubscription.channel)
        return if (channel == null) {
            NotificationDispatchingResult.CHANNEL_UNKNOWN
        } else if (channel.enabled) {
            // Dispatching
            if (dispatchEventToChannel(event, eventSubscription, channel)) {
                NotificationDispatchingResult.SENT
            } else {
                NotificationDispatchingResult.UNSENT
            }
        } else {
            NotificationDispatchingResult.CHANNEL_DISABLED
        }
    }

    private fun <C, R> dispatchEventToChannel(
        event: Event,
        eventSubscription: EventSubscription,
        channel: NotificationChannel<C, R>,
    ): Boolean {
        val channelConfig = channel.validate(eventSubscription.channelConfig)
        return if (channelConfig.isOk()) {
            val item = Notification(
                source = getNotificationSourceData(eventSubscription),
                channel = eventSubscription.channel,
                channelConfig = eventSubscription.channelConfig,
                event = event,
                template = eventSubscription.contentTemplate,
            )
            // Publication of the event
            notificationQueue.publish(item)
        } else {
            // Log the channel validation message as an error
            applicationLogService.log(
                ApplicationLogEntry.error(
                    channelConfig.exception,
                    NameDescription.nd(
                        "notification-channel-config-invalid",
                        "Notification channel configuration invalid"
                    ),
                    "Cannot validate the configuration for a channel"
                ).withDetail(
                    "channelConfig", eventSubscription.channelConfig.toPrettyString()
                ).withDetail(
                    "channelConfigMessage", channelConfig.message
                )
            )
            // Not processed
            false
        }
    }

    private fun getNotificationSourceData(eventSubscription: EventSubscription): NotificationSourceData {
        return if (eventSubscription.projectEntity != null) {
            entitySubscriptionNotificationSource.createData(
                EntitySubscriptionNotificationSourceDataType(
                    entityType = eventSubscription.projectEntity.projectEntityType,
                    entityId = eventSubscription.projectEntity.id(),
                    subscriptionName = eventSubscription.name,
                )
            )
        } else {
            globalSubscriptionNotificationSource.createData(
                GlobalSubscriptionNotificationSourceDataType(
                    subscriptionName = eventSubscription.name,
                )
            )
        }
    }

}