package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.job.JobScheduler
import org.springframework.stereotype.Component

@Component
class GQLRootQueryJobCategories(
    private val gqlTypeJobCategory: GQLTypeJobCategory,
    private val jobScheduler: JobScheduler,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("jobCategories")
            .description("List of all job categories and their types (at any given time)")
            .type(listType(gqlTypeJobCategory.typeRef))
            .dataFetcher {
                jobScheduler.allJobKeys
                    .map { it.type.category }
                    .distinctBy { it.key }
                    .sortedBy { it.name }
            }
            .build()
}