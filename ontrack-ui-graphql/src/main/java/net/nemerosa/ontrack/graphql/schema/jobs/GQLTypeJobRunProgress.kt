package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.job.JobRunProgress
import org.springframework.stereotype.Component

@Component
class GQLTypeJobRunProgress : GQLType {

    override fun getTypeName(): String = JobRunProgress::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(JobRunProgress::class))
            .intField(JobRunProgress::percentage)
            .stringField(JobRunProgress::message)
            .stringField(JobRunProgress::text)
            .build()
}