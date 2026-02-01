package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.localDateTimeField
import net.nemerosa.ontrack.graphql.support.longField
import net.nemerosa.ontrack.model.job.JobHistogramItem
import org.springframework.stereotype.Component

@Component
class GQLTypeJobHistogramItem : GQLType {

    override fun getTypeName(): String = JobHistogramItem::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Histogram item for a job")
            .localDateTimeField(JobHistogramItem::from)
            .localDateTimeField(JobHistogramItem::to)
            .intField(JobHistogramItem::count)
            .intField(JobHistogramItem::errorCount)
            .longField(JobHistogramItem::avgDurationMs)
            .longField(JobHistogramItem::minDurationMs)
            .longField(JobHistogramItem::maxDurationMs)
            .booleanField(JobHistogramItem::error)
            .build()
}
