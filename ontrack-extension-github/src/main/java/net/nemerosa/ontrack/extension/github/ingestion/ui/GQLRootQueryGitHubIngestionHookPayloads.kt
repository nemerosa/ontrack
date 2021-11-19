package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.listInputType
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitHubIngestionHookPayloads(
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlGitHubIngestionHookPayload: GQLGitHubIngestionHookPayload,
    private val gqlEnumIngestionHookPayloadStatus: GQLEnumIngestionHookPayloadStatus,
    private val ingestionHookPayloadStorage: IngestionHookPayloadStorage,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any?, IngestionHookPayload>(
            cache = GQLTypeCache(),
            fieldName = "gitHubIngestionHookPayloads",
            fieldDescription = "List of payloads received by the GitHub Ingestion Hook payload",
            itemType = gqlGitHubIngestionHookPayload,
            arguments = listOf(
                GraphQLArgument.newArgument()
                    .name(ARG_STATUSES)
                    .description("Filter on the statuses")
                    .type(
                        listInputType(
                            gqlEnumIngestionHookPayloadStatus.getTypeRef(),
                            nullable = true,
                        )
                    )
                    .build(),
                GraphQLArgument.newArgument()
                    .name(ARG_UUID)
                    .description("Filter on the UUID")
                    .type(GraphQLString)
                    .build(),
                GraphQLArgument.newArgument()
                    .name(ARG_GITHUB_DELIVERY)
                    .description("Filter on the GitHub Delivery ID")
                    .type(GraphQLString)
                    .build(),
                GraphQLArgument.newArgument()
                    .name(ARG_GITHUB_EVENT)
                    .description("Filter on the GitHub Event")
                    .type(GraphQLString)
                    .build(),
                GraphQLArgument.newArgument()
                    .name(ARG_REPOSITORY)
                    .description("Filter on the GitHub repository name")
                    .type(GraphQLString)
                    .build(),
                GraphQLArgument.newArgument()
                    .name(ARG_OWNER)
                    .description("Filter on the GitHub repository owner name")
                    .type(GraphQLString)
                    .build(),
                GraphQLArgument.newArgument()
                    .name(ARG_ROUTING)
                    .description("Filter on the routing information")
                    .type(GraphQLString)
                    .build(),
                GraphQLArgument.newArgument()
                    .name(ARG_QUEUE)
                    .description("Filter on the queue information")
                    .type(GraphQLString)
                    .build(),
            ),
            itemPaginatedListProvider = { env, _, offset, size ->
                val uuid: String? = env.getArgument(ARG_UUID)
                if (uuid != null) {
                    val payload = ingestionHookPayloadStorage.findByUUID(uuid)
                    if (payload != null) {
                        PaginatedList.ofOne(payload)
                    } else {
                        PaginatedList.empty()
                    }
                } else {
                    val delivery: String? = env.getArgument(ARG_GITHUB_DELIVERY)
                    val event: String? = env.getArgument(ARG_GITHUB_EVENT)
                    val repository: String? = env.getArgument(ARG_REPOSITORY)
                    val owner: String? = env.getArgument(ARG_OWNER)
                    val routing: String? = env.getArgument(ARG_ROUTING)
                    val queue: String? = env.getArgument(ARG_QUEUE)
                    val statusesList: List<String>? = env.getArgument(ARG_STATUSES)
                    val statuses = statusesList?.map {
                        IngestionHookPayloadStatus.valueOf(it)
                    }
                    val count = ingestionHookPayloadStorage.count(
                        statuses = statuses,
                        gitHubDelivery = delivery,
                        gitHubEvent = event,
                        repository = repository,
                        owner = owner,
                        routing = routing,
                        queue = queue,
                    )
                    val items = ingestionHookPayloadStorage.list(offset, size,
                        statuses = statuses,
                        gitHubDelivery = delivery,
                        gitHubEvent = event,
                        repository = repository,
                        owner = owner,
                        routing = routing,
                        queue = queue,
                    )
                    PaginatedList.create(
                        items = items,
                        offset = offset,
                        pageSize = size,
                        total = count,
                    )
                }
            },
        )

    companion object {
        private const val ARG_UUID = "uuid"
        private const val ARG_STATUSES = "statuses"
        private const val ARG_GITHUB_DELIVERY = "gitHubDelivery"
        private const val ARG_GITHUB_EVENT = "gitHubEvent"
        private const val ARG_REPOSITORY = "repository"
        private const val ARG_OWNER = "owner"
        private const val ARG_ROUTING = "routing"
        private const val ARG_QUEUE = "queue"
    }

}