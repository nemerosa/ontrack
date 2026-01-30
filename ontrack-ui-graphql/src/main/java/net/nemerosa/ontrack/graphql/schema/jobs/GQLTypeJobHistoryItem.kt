package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.model.job.JobHistoryItem
import org.springframework.stereotype.Component

@Component
class GQLTypeJobHistoryItem : GQLType {

    override fun getTypeName(): String = JobHistoryItem::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("History item for a job")
            .intField(JobHistoryItem::id)
            .stringField(JobHistoryItem::jobCategory)
            .stringField(JobHistoryItem::jobType)
            .stringField(JobHistoryItem::jobKey)
            .localDateTimeField(JobHistoryItem::startedAt)
            .localDateTimeField(JobHistoryItem::endedAt)
            .durationField(JobHistoryItem::duration)
            .enumField(JobHistoryItem::status)
            .stringField(JobHistoryItem::message)
            .field {
                it.name("durationMs")
                    .description("Duration in milliseconds")
                    .type(GraphQLInt)
                    .dataFetcher { env ->
                        val item = env.getSource<JobHistoryItem>()!!
                        item.duration.toMillis()
                    }
            }
            .build()
}

//duration: Duration = Duration.between(startedAt, endedAt)