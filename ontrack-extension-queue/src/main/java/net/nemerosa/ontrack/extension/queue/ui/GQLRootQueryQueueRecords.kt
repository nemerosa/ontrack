package net.nemerosa.ontrack.extension.queue.ui

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.queue.record.QueueRecord
import net.nemerosa.ontrack.extension.queue.record.QueueRecordQueryFilter
import net.nemerosa.ontrack.extension.queue.record.QueueRecordQueryService
import net.nemerosa.ontrack.extension.queue.record.QueueRecordState
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumArgument
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
                    itemType = gqlTypeQueueRecord.typeName,
                    itemPaginatedListProvider = { env, _, offset, size ->
                        getPaginatedList(env, offset, size)
                    },
                    arguments = listOf(
                            stringArgument(ARG_ID, "Queue message ID"),
                            stringArgument(ARG_PROCESSOR, "Queue processor"),
                            enumArgument<QueueRecordState>(ARG_STATE, "Queue message state"),
                            stringArgument(ARG_ROUTING, "Routing key"),
                            stringArgument(ARG_QUEUE, "Queue name"),
                            stringArgument(ARG_TEXT, "Text in the payload"),
                    )
            )

    private fun getPaginatedList(env: DataFetchingEnvironment, offset: Int, size: Int): PaginatedList<QueueRecord> {
        val filter = QueueRecordQueryFilter(
            id = env.getArgument(ARG_ID),
            processor = env.getArgument(ARG_PROCESSOR),
            state = env.getArgument<String?>(ARG_STATE)?.let { QueueRecordState.valueOf(it) },
            routingKey = env.getArgument(ARG_ROUTING),
            queueName = env.getArgument(ARG_QUEUE),
            text = env.getArgument(ARG_TEXT),
        )
        return queueRecordQueryService.findByFilter(filter, offset, size)
    }

    companion object {
        const val ARG_ID = "id"
        const val ARG_PROCESSOR = "processor"
        const val ARG_STATE = "state"
        const val ARG_ROUTING = "routing"
        const val ARG_QUEUE = "queue"
        const val ARG_TEXT = "text"
    }
}
