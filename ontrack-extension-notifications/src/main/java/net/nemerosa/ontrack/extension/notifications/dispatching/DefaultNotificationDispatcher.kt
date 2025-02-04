package net.nemerosa.ontrack.extension.notifications.dispatching

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.model.NotificationSourceData
import net.nemerosa.ontrack.extension.notifications.model.createData
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueuePayload
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueProcessor
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueSourceData
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueSourceExtension
import net.nemerosa.ontrack.extension.notifications.sources.EntitySubscriptionNotificationSource
import net.nemerosa.ontrack.extension.notifications.sources.EntitySubscriptionNotificationSourceDataType
import net.nemerosa.ontrack.extension.notifications.sources.GlobalSubscriptionNotificationSource
import net.nemerosa.ontrack.extension.notifications.sources.GlobalSubscriptionNotificationSourceDataType
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.queue.source.createQueueSource
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.dehydrate
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class DefaultNotificationDispatcher(
    private val notificationChannelRegistry: NotificationChannelRegistry,
    private val queueDispatcher: QueueDispatcher,
    private val entitySubscriptionNotificationSource: EntitySubscriptionNotificationSource,
    private val globalSubscriptionNotificationSource: GlobalSubscriptionNotificationSource,
    private val notificationQueueProcessor: NotificationQueueProcessor,
    private val notificationQueueSourceExtension: NotificationQueueSourceExtension,
) : NotificationDispatcher {

    private val logger: Logger = LoggerFactory.getLogger(DefaultNotificationDispatcher::class.java)

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
            val payload = NotificationQueuePayload(
                id = UUID.randomUUID().toString(),
                source = getNotificationSourceData(eventSubscription),
                channel = eventSubscription.channel,
                channelConfig = eventSubscription.channelConfig,
                serializableEvent = event.dehydrate(),
                template = eventSubscription.contentTemplate,
            )
            // Publication of the event
            queueDispatcher.dispatch(
                queueProcessor = notificationQueueProcessor,
                payload = payload,
                source = notificationQueueSourceExtension.createQueueSource(
                    NotificationQueueSourceData(
                        projectEntityID = eventSubscription.projectEntity?.toProjectEntityID(),
                        subscriptionName = eventSubscription.name,
                    )
                )
            )
            // OK
            true
        } else {
            // Log the channel validation message as an error
            logger.error(
                "Notification channel configuration invalid: ${eventSubscription.channelConfig.toPrettyString()} (${channelConfig.message})",
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