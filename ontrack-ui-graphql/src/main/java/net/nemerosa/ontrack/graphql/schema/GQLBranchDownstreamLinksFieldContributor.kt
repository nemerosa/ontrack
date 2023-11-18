package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.support.intArgument
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.links.BranchLinksService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * Contributes the `downstreamLinks` field to the `Branch` type to get the list
 * of downstream branches. These branches are the ones the source branch depends on.
 */
@Component
class GQLBranchDownstreamLinksFieldContributor(
    private val gqlTypeBranchLink: GQLTypeBranchLink,
    private val branchLinksService: BranchLinksService,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition> {
        if (projectEntityType == ProjectEntityType.BRANCH) {
            return listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("downstreamLinks")
                    .description("List of branches this branch depends on.")
                    .type(listType(gqlTypeBranchLink.typeRef))
                    .argument(
                        intArgument(
                            ARG_BUILDS,
                            "Number of builds to use to get the dependencies",
                        )
                    )
                    .dataFetcher { env ->
                        val branch: Branch = env.getSource()
                        val builds: Int? = env.getArgument(ARG_BUILDS)
                        branchLinksService.getDownstreamDependencies(branch, builds ?: DEFAULT_BUILDS)
                    }
                    .build()
            )
        } else {
            return emptyList()
        }
    }

    companion object {
        const val ARG_BUILDS = "builds"
        const val DEFAULT_BUILDS = 5
    }
}
