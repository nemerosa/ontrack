package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ProjectEntityID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class EventSubscriptionMutations(
    private val eventSubscriptionService: EventSubscriptionService,
    private val structureService: StructureService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(

        simpleMutation(
            name = "subscribeToEvents",
            description = "Creates a subscription to a list of events",
            input = SubscribeToEventsInput::class,
            outputName = "subscription",
            outputDescription = "Saved subscription",
            outputType = EventSubscriptionPayload::class,
        ) { input ->
            val projectEntity = input.projectEntity?.run {
                type.getEntityFn(structureService).apply(ID.of(id))
            }
            val record = eventSubscriptionService.subscribe(
                EventSubscription(
                    channel = input.channel,
                    channelConfig = input.channelConfig,
                    events = input.events.toSet(),
                    projectEntity = projectEntity,
                    keywords = input.keywords,
                    disabled = false,
                )
            )
            EventSubscriptionPayload(
                id = record.id,
                channel = record.data.channel,
                channelConfig = record.data.channelConfig,
                events = record.data.events.toList(),
                keywords = record.data.keywords,
                disabled = record.data.disabled,
            )
        },

        unitMutation<DeleteSubscriptionInput>(
            name = "deleteSubscription",
            description = "Deletes an existing subscription using its ID",
        ) { input ->
            val projectEntity = input.projectEntity?.run {
                type.getEntityFn(structureService).apply(ID.of(id))
            }
            eventSubscriptionService.deleteSubscriptionById(projectEntity, input.id)
        },

        simpleMutation(
            name = "disableSubscription",
            description = "Disables an existing subscription",
            input = DisableSubscriptionInput::class,
            outputName = "subscription",
            outputDescription = "Saved subscription",
            outputType = EventSubscriptionPayload::class,
        ) { input ->
            val projectEntity = input.projectEntity?.run {
                type.getEntityFn(structureService).apply(ID.of(id))
            }
            val record = eventSubscriptionService.disableSubscriptionById(projectEntity, input.id)
            EventSubscriptionPayload(
                id = record.id,
                channel = record.data.channel,
                channelConfig = record.data.channelConfig,
                events = record.data.events.toList(),
                keywords = record.data.keywords,
                disabled = record.data.disabled,
            )
        },

        simpleMutation(
            name = "enableSubscription",
            description = "Enables an existing subscription",
            input = EnableSubscriptionInput::class,
            outputName = "subscription",
            outputDescription = "Saved subscription",
            outputType = EventSubscriptionPayload::class,
        ) { input ->
            val projectEntity = input.projectEntity?.run {
                type.getEntityFn(structureService).apply(ID.of(id))
            }
            val record = eventSubscriptionService.enableSubscriptionById(projectEntity, input.id)
            EventSubscriptionPayload(
                id = record.id,
                channel = record.data.channel,
                channelConfig = record.data.channelConfig,
                events = record.data.events.toList(),
                keywords = record.data.keywords,
                disabled = record.data.disabled,
            )
        },
    )
}

@APIDescription("Subscription deletion")
data class DeleteSubscriptionInput(
    @APIDescription("Target project entity (null for global events)")
    @TypeRef(embedded = true, suffix = "Input")
    val projectEntity: ProjectEntityID?,
    @APIDescription("ID of the subscription to delete")
    val id: String,
)

@APIDescription("Subscription disabling")
data class DisableSubscriptionInput(
    @APIDescription("Target project entity (null for global events)")
    @TypeRef(embedded = true, suffix = "Input")
    val projectEntity: ProjectEntityID?,
    @APIDescription("ID of the subscription to disable")
    val id: String,
)

@APIDescription("Subscription enabling")
data class EnableSubscriptionInput(
    @APIDescription("Target project entity (null for global events)")
    @TypeRef(embedded = true, suffix = "Input")
    val projectEntity: ProjectEntityID?,
    @APIDescription("ID of the subscription to enable")
    val id: String,
)

@APIDescription("Subscription to events")
data class SubscribeToEventsInput(
    @APIDescription("Target project entity (null for global events)")
    @TypeRef(embedded = true, suffix = "Input")
    val projectEntity: ProjectEntityID?,
    @APIDescription("Channel to send this event to")
    val channel: String,
    @APIDescription("Channel configuration")
    val channelConfig: JsonNode,
    @APIDescription("List of events types to subscribe to")
    @ListRef
    val events: List<String>,
    @APIDescription("Optional space-separated list of tokens to look for in the events")
    val keywords: String?,
)

