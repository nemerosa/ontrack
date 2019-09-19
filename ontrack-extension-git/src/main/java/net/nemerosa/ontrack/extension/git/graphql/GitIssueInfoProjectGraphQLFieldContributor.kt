package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GitIssueInfoProjectGraphQLFieldContributor(
        private val ontrackGitIssueInfoGQLType: OntrackGitIssueInfoGQLType,
        private val gitService: GitService
) : GQLProjectEntityFieldContributor {

    override fun getFields(
            projectEntityClass: Class<out ProjectEntity>,
            projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
            if (projectEntityType == ProjectEntityType.PROJECT) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("gitIssueInfo")
                                .description("Information about an issue in the project")
                                .argument {
                                    it.name("token")
                                            .description("Key or full name of an issue")
                                            .type(GraphQLNonNull(Scalars.GraphQLString))
                                }
                                .argument {
                                    it.name("first")
                                            .description("Flag to display only the first branch per type")
                                            .type(Scalars.GraphQLBoolean)
                                }
                                .type(ontrackGitIssueInfoGQLType.typeRef)
                                .dataFetcher { environment ->
                                    val project: Project = environment.getSource()
                                    val token: String = environment.getArgument("token")
                                    val first: Boolean? = environment.getArgument("first")
                                    val gitIssueInfo = gitService.getIssueProjectInfo(project.id, token)
                                    if (first != null && first) {
                                        gitIssueInfo?.first()
                                    } else {
                                        gitIssueInfo
                                    }
                                }
                                .build()
                )
            } else {
                null
            }


}