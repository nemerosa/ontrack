package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

/**
 * Contributes the `displayName` field to the `Branch` type to display the branch's display name.
 */
@Component
class GQLBranchDisplayNameFieldContributor(
    private val branchDisplayNameService: BranchDisplayNameService
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition> {
        if (projectEntityType == ProjectEntityType.BRANCH) {
            return listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("displayName")
                    .description("Display name for this branch or the branch's name if not available")
                    .type(GraphQLNonNull(GraphQLString))
                    .dataFetcher { env ->
                        val branch: Branch = env.getSource()
                        branchDisplayNameService.getBranchDisplayName(branch)
                    }
                    .build()
            )
        } else {
            return emptyList()
        }
    }
}
