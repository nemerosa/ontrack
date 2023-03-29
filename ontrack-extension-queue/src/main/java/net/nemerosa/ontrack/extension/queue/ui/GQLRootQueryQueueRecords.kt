package net.nemerosa.ontrack.extension.queue.ui

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.queue.record.QueueRecord
import net.nemerosa.ontrack.extension.queue.record.QueueRecordQueryFilter
import net.nemerosa.ontrack.extension.queue.record.QueueRecordQueryService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.stereotype.Component

/**
 * Getting a paginated list of queue messages.
 */
@Component
class GQLRootQueryQueueRecords(
    private val gqlTypeQueueRecord: GQLTypeQueueRecord,
    private val queueRecordQueryService: QueueRecordQueryService,
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any?, QueueRecord>(
            cache = GQLTypeCache(),
            fieldName = "queueRecords",
            fieldDescription = "Getting a paginated list of queue messages.",
            itemType = gqlTypeQueueRecord,
            itemPaginatedListProvider = { env, _, offset, size ->
                getPaginatedList(env, offset, size)
            },
            arguments = listOf(
                stringArgument(ARG_ID, "Queue message ID"),
            )
        )

    private fun getPaginatedList(env: DataFetchingEnvironment, offset: Int, size: Int): PaginatedList<QueueRecord> {
        val filter = QueueRecordQueryFilter(
            id = env.getArgument(ARG_ID),
        )
        return queueRecordQueryService.findByFilter(filter, offset, size)
    }

    companion object {
        const val ARG_ID = "id"
    }
}
