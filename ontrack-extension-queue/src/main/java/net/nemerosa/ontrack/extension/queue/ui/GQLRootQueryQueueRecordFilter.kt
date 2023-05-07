package net.nemerosa.ontrack.extension.queue.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.queue.record.QueueRecordState
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.toNotNull
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
