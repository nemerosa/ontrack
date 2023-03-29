package net.nemerosa.ontrack.extension.queue.ui

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.queue.record.QueueRecord
import net.nemerosa.ontrack.extension.queue.record.QueueRecordQueryFilter
import net.nemerosa.ontrack.extension.queue.record.QueueRecordQueryService
import net.nemerosa.ontrack.extension.queue.record.QueueRecordState
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.stereotype.Component

/**
 * Getting information for the queue record filtering.
 */
@Component
class GQLRootQueryQueueRecordFilter(
    private val gqlTypeQueueRecordFilterInfo: GQLTypeQueueRecordFilterInfo,
    private val queueProcessors: List<QueueProcessor<*>>,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("queueRecordFilterInfo")
            .description("Information for the queue record query filter")
            .type(gqlTypeQueueRecordFilterInfo.typeRef.toNotNull())
            .dataFetcher {
                QueueRecordFilterInfo(
                    processors = queueProcessors.map { it.id }.sorted(),
                    states = QueueRecordState.values().map { it.name },
                )
            }
            .build()

}
