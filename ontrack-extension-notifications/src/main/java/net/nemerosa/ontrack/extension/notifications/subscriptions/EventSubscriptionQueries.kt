package net.nemerosa.ontrack.extension.notifications.subscriptions

import graphql.GraphQLContext
import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryEventSubscriptions(
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlTypeEventSubscriptionPayload: GQLTypeEventSubscriptionPayload,
    private val gqlInputEventSubscriptionFilter: GQLInputEventSubscriptionFilter,
    private val eventSubscriptionService: EventSubscriptionService,
    private val securityService: SecurityService,
    private val structureService: StructureService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any?, EventSubscriptionPayload>(
            cache = GQLTypeCache(),
            fieldName = "eventSubscriptions",
            fieldDescription = "List of event subscriptions",
            itemType = gqlTypeEventSubscriptionPayload.typeName,
            arguments = listOf(
                GraphQLArgument.newArgument()
                    .name("filter")
                    .description("Filter for the subscriptions")
                    .type(gqlInputEventSubscriptionFilter.typeRef)
                    .build()
            ),
            additionalFields = listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("writeSubscriptionGranted")
                    .description("Can the user create subscriptions in the current context")
                    .type(GraphQLBoolean.toNotNull())
                    .dataFetcher { env ->
                        val projectEntity: ProjectEntity? = env.getContext<GraphQLContext>().get(CONTEXT_PROJECT_ENTITY)
                        if (projectEntity != null) {
                            securityService.isProjectFunctionGranted(projectEntity,
                                ProjectSubscriptionsWrite::class.java)
                        } else {
                            securityService.isGlobalFunctionGranted(GlobalSubscriptionsManage::class.java)
                        }
                    }
                    .build()
            ),
            itemPaginatedListProvider = { env, _, offset, size ->
                // Parsing of the filter
                val filter = env.getArgument<Any>("filter").asJson().parse<EventSubscriptionFilter>()
                    // Pagination from the root arguments
                    .withPage(offset, size)
                // Setting the context
                getProjectEntity(filter)?.let { env.getContext<GraphQLContext>().put(CONTEXT_PROJECT_ENTITY, it) }
                // Getting the list
                eventSubscriptionService.filterSubscriptions(filter).map {
                    EventSubscriptionPayload(
                        id = it.id,
                        channel = it.data.channel,
                        channelConfig = it.data.channelConfig,
                        events = it.data.events.toList(),
                        keywords = it.data.keywords,
                        disabled = it.data.disabled,
                        origin = it.data.origin,
                    )
                }
            }
        )

    private fun getProjectEntity(filter: EventSubscriptionFilter) = filter.entity?.run {
        type.getEntityFn(structureService).apply(ID.of(id))
    }

    companion object {
        const val CONTEXT_PROJECT_ENTITY = "eventSubscriptionsFilterProjectEntity"
    }

}