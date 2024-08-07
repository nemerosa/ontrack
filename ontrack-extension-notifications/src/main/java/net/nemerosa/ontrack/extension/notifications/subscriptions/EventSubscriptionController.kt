package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.SelectableItem
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Form controller for subscriptions.
 */
@RestController
@RequestMapping("/extension/notifications/subscription")
class EventSubscriptionController(
    private val structureService: StructureService,
    private val eventSubscriptionService: EventSubscriptionService,
    private val notificationChannelRegistry: NotificationChannelRegistry,
    private val eventFactory: EventFactory,
) {

    /**
     * Creation form
     */
    @GetMapping("create")
    fun create(): Form = form(null)

    /**
     * Edition form for a global subscription
     */
    @GetMapping("global/{id}/update")
    fun updateGlobal(@PathVariable("id") id: String): Form = form(
        eventSubscriptionService.findSubscriptionByName(null, id)
            ?: throw EventSubscriptionNameNotFoundException(null, id)
    )

    /**
     * Edition form for an entity subscription
     */
    @GetMapping("entity/{entityType}/{entityId}/{id}/update")
    fun updateEntity(
        @PathVariable("entityType") entityType: ProjectEntityType,
        @PathVariable("entityId") entityId: Int,
        @PathVariable("id") id: String,
    ): Form =
        entityType.getEntityFn(structureService).apply(ID.of(entityId)).let { entity ->
            form(
                eventSubscriptionService.findSubscriptionByName(entity, id)
                    ?: throw EventSubscriptionNameNotFoundException(entity, id)
            )
        }

    private fun form(subscription: EventSubscription?): Form = Form.create()
        // name
        .textField(EventSubscription::name, subscription?.name)
        // events
        .with(
            MultiSelection.of(EventSubscription::events.name)
                .label("Events")
                .help(getPropertyDescription(EventSubscription::events))
                .items(
                    eventFactory.eventTypes
                        .sortedBy { it.id }
                        .map { eventType ->
                            SelectableItem(
                                subscription != null && eventType.id in subscription.events,
                                eventType.id,
                                eventType.id,
                            )
                        }
                )
        )
        // keywords
        .with(
            Text.of(EventSubscription::keywords.name)
                .label("Keywords")
                .help(getPropertyDescription(EventSubscription::keywords))
                .optional()
                .value(subscription?.keywords)
        )
        // channel
        .with(
            ServiceConfigurator.of(EventSubscription::channel.name)
                .label("Channel")
                .help(getPropertyDescription(EventSubscription::channel))
                .sources(
                    notificationChannelRegistry.channels
                        .map { channel ->
                            ServiceConfigurationSource(
                                channel.type,
                                if (channel.enabled) {
                                    channel.type
                                } else {
                                    "${channel.type} (disabled)"
                                },
                                channelConfigForm(channel)
                            )
                        }
                )
                .value(
                    subscription?.run {
                        ServiceConfiguration(
                            id = channel,
                            data = channelConfig,
                        )
                    }
                )
        )
        // Content template
        .with(
            Memo.of(EventSubscription::contentTemplate.name)
                .label("Content")
                .help(getPropertyDescription(EventSubscription::contentTemplate))
                .optional()
                .value(subscription?.contentTemplate)
        )

    private fun <C, R> channelConfigForm(channel: NotificationChannel<C, R>): Form =
        channel.getForm(null)


}