package net.nemerosa.ontrack.extension.queue.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.queue.record.QueueRecordQueryService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQueryQueueRecord(
    private val gqlTypeQueueRecord: GQLTypeQueueRecord,
    private val queueRecordQueryService: QueueRecordQueryService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("queueRecord")
            .description("Get a queue record using its ID")
            .type(gqlTypeQueueRecord.typeRef)
            .argument(
                stringArgument(
                    ARG_ID,
                    "ID of the record to find",
                    nullable = false
                )
            )
            .dataFetcher { env ->
                val id: String = env.getArgument(ARG_ID)
                queueRecordQueryService.findByQueuePayloadID(id)
            }
            .build()

    companion object {
        const val ARG_ID = "id"
    }
}