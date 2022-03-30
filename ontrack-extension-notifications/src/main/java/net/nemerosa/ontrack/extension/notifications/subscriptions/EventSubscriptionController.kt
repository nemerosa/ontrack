package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.graphql.support.getPropertyDescription
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.NameValue
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
        eventSubscriptionService.findSubscriptionById(null, id)
            ?: throw EventSubscriptionIdNotFoundException(null, id)
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
                eventSubscriptionService.findSubscriptionById(entity, id)
                    ?: throw EventSubscriptionIdNotFoundException(entity, id)
            )
        }

    private fun form(subscription: EventSubscription?): Form = Form.create()
        // TODO events
        // TODO keywords
        // channel
        .with(
            Selection.of(EventSubscription::channel.name)
                .label("Channel")
                .help(getPropertyDescription(EventSubscription::channel))
                .items(
                    notificationChannelRegistry.channels
                        .map {
                            NameValue(
                                it.type,
                                if (it.enabled) {
                                    it.type
                                } else {
                                    "${it.type} (disabled)"
                                }
                            )
                        }
                )
                .itemId(NameValue::name.name)
                .itemName(NameValue::value.name)
                .value(subscription?.channel)
        )
    // TODO channelConfig


}