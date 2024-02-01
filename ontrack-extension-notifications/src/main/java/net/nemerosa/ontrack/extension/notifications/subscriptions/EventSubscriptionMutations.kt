package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import graphql.Scalars
import graphql.schema.*
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class EventSubscriptionMutations(
    private val eventSubscriptionService: EventSubscriptionService,
    private val structureService: StructureService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation>
        get() =
            genericMutations +
                    ProjectEntityType.values().map { type ->
                        createEntitySubscriptionMutation(type)
                    }

    private fun createEntitySubscriptionMutation(type: ProjectEntityType): Mutation =
        object : Mutation {

            override val name: String = "subscribe${type.typeName}ToEvents"

            override val description: String =
                "Set a list of subscriptions on a ${type.displayName} identified by name."

            override fun inputFields(dictionary: MutableSet<GraphQLType>): List<GraphQLInputObjectField> =
                type.names.map {
                    name(it)
                } + listOf(
                    stringInputField(SubscribeToEventsInput::channel),
                    jsonInputField(SubscribeToEventsInput::channelConfig),
                    stringListInputField(SubscribeToEventsInput::events),
                    stringInputField(SubscribeToEventsInput::keywords),
                    stringInputField(SubscribeToEventsInput::contentTemplate),
                )

            override val outputFields: List<GraphQLFieldDefinition> = listOf(
                objectField(
                    type = EventSubscriptionPayload::class,
                    name = "subscription",
                    description = "Saved subscription"
                )
            )

            override fun fetch(env: DataFetchingEnvironment): Any {
                // Loads the entity
                val names = type.names.associateWith { name ->
                    getRequiredMutationInputField<String>(env, name)
                }
                val entity: ProjectEntity = type.loadByNames(structureService, names)
                    ?: throw EntityNotFoundByNameException(type, names)
                // Creates the payload
                val payload = createEventSubscriptionPayload(
                    projectEntity = entity,
                    channel = getRequiredMutationInputField(env, SubscribeToEventsInput::channel.name),
                    channelConfig = getRequiredMutationInputField(env, SubscribeToEventsInput::channelConfig.name),
                    events = getRequiredMutationInputField(env, SubscribeToEventsInput::events.name),
                    keywords = getMutationInputField(env, SubscribeToEventsInput::keywords.name),
                    contentTemplate = getMutationInputField(env, SubscribeToEventsInput::contentTemplate.name),
                )
                // OK
                return mapOf("subscription" to payload)
            }
        }

    private val genericMutations: List<Mutation>
        get() = listOf(

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
                createEventSubscriptionPayload(
                    projectEntity = projectEntity,
                    channel = input.channel,
                    channelConfig = input.channelConfig,
                    events = input.events,
                    keywords = input.keywords,
                    contentTemplate = input.contentTemplate,
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
                    origin = record.data.origin,
                    contentTemplate = record.data.contentTemplate,
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
                    origin = record.data.origin,
                    contentTemplate = record.data.contentTemplate,
                )
            },
        )

    private fun createEventSubscriptionPayload(
        projectEntity: ProjectEntity?,
        channel: String,
        channelConfig: JsonNode,
        events: List<String>,
        keywords: String?,
        contentTemplate: String?,
    ): EventSubscriptionPayload {
        val record = eventSubscriptionService.subscribe(
            EventSubscription(
                channel = channel,
                channelConfig = channelConfig,
                events = events.toSet(),
                projectEntity = projectEntity,
                keywords = keywords,
                disabled = false,
                origin = EventSubscriptionOrigins.API,
                contentTemplate = contentTemplate,
            )
        )
        return EventSubscriptionPayload(
            id = record.id,
            channel = record.data.channel,
            channelConfig = record.data.channelConfig,
            events = record.data.events.toList(),
            keywords = record.data.keywords,
            disabled = record.data.disabled,
            origin = record.data.origin,
            contentTemplate = record.data.contentTemplate,
        )
    }

    private fun name(name: String): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
        .name(name)
        .description("${name.replaceFirstChar { it.titlecase() }} name")
        .type(GraphQLNonNull(Scalars.GraphQLString))
        .build()
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
    @APIDescription("Optional template to use for the message")
    val contentTemplate: String?,
)

