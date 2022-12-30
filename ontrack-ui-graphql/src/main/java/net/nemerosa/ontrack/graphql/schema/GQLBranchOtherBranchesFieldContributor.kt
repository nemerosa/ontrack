package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GQLBranchOtherBranchesFieldContributor
@Autowired
constructor(
    private val structureService: StructureService
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition> {
        if (projectEntityType == ProjectEntityType.BRANCH) {
            return listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("otherBranches")
                    .description("List of other branches in the same project")
                    .type(listType(GraphQLTypeReference(GQLTypeBranch.BRANCH)))
                    .dataFetcher { env ->
                        val branch: Branch = env.getSource()
                        structureService.filterBranchesForProject(
                                project = branch.project,
                                filter = BranchFilter(
                                        order = true,
                                        count = 10,
                                )
                        ).filter { it.id != branch.id }
                    }
                    .build()
            )
        } else {
            return listOf()
        }
    }
}
