package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.job.JobKey
import org.springframework.stereotype.Component

@Component
class GQLTypeJobKey : GQLType {

    override fun getTypeName(): String = JobKey::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(JobKey::class))
            .stringField(JobKey::id)
            .field(JobKey::type)
            .build()
}