package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.job.JobState
import org.springframework.stereotype.Component

@Component
class GQLRootQueryJobStateInfos(
    private val gqlTypeJobStateInfo: GQLTypeJobStateInfo,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("jobStateInfos")
            .description("List of job states")
            .type(listType(gqlTypeJobStateInfo.typeRef))
            .dataFetcher {
                JobState.values()
            }
            .build()
}