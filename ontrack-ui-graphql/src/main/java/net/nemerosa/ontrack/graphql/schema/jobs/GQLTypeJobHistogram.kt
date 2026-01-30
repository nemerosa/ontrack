package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.durationField
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.graphql.support.localDateTimeField
import net.nemerosa.ontrack.model.job.JobHistogram
import org.springframework.stereotype.Component

@Component
class GQLTypeJobHistogram : GQLType {

    override fun getTypeName(): String = JobHistogram::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Histogram of run durations and status for a job")
            .localDateTimeField(JobHistogram::from)
            .localDateTimeField(JobHistogram::to)
            .durationField(JobHistogram::interval)
            .listField(JobHistogram::items)
            .build()
}
