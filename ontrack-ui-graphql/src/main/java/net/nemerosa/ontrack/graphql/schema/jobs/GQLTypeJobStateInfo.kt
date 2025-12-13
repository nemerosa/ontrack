package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.job.JobState
import org.springframework.stereotype.Component

@Component
class GQLTypeJobStateInfo : GQLType {

    override fun getTypeName(): String = "JobStateInfo"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Job state information")
            .field {
                it.name("name")
                    .description("Name of the state")
                    .type(GraphQLString.toNotNull())
                    .dataFetcher { env ->
                        val state: JobState = env.getSource()!!
                        state.name
                    }
            }
            .stringField(JobState::displayName)
            .stringField(JobState::description)
            .build()

}