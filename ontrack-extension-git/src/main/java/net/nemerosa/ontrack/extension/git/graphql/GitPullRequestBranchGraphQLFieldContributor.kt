package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GitPullRequestBranchGraphQLFieldContributor(
        private val gitPullRequestGQLType: GitPullRequestGQLType,
        private val gitService: GitService
) : GQLProjectEntityFieldContributor {

    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>? =
            if (projectEntityType == ProjectEntityType.BRANCH) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name(FIELD_PULL_REQUEST)
                                .description("Pull request (if any) linked to the branch")
                                .type(gitPullRequestGQLType.typeRef)
                                .dataFetcher { env ->
                                    val branch: Branch = env.getSource()
                                    gitService.getBranchAsPullRequest(branch)
                                }
                                .build()
                )
            } else {
                null
            }

    companion object {
        const val FIELD_PULL_REQUEST = "pullRequest"
    }

}