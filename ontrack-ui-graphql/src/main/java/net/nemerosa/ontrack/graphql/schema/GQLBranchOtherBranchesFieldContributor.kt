package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GQLBranchOtherBranchesFieldContributor
@Autowired
constructor(
        private val structureService: StructureService
) : GQLProjectEntityFieldContributor {
    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition> {
        if (projectEntityType == ProjectEntityType.BRANCH) {
            return listOf(
                    GraphQLFieldDefinition.newFieldDefinition()
                            .name("otherBranches")
                            .description("List of other branches in the same project")
                            .type(GraphqlUtils.stdList(GraphQLTypeReference(GQLTypeBranch.BRANCH)))
                            .dataFetcher(GraphqlUtils.fetcher(
                                    Branch::class.java,
                                    { branch ->
                                        structureService.getBranchesForProject(branch.project.id)
                                                .filter { it.id != branch.id }
                                    }
                            ))
                            .build()
            )
        } else {
            return listOf()
        }
    }
}
