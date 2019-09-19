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
class GitCommitInfoProjectGraphQLFieldContributor(
        private val ontrackGitCommitInfoGQLType: OntrackGitCommitInfoGQLType,
        private val gitService: GitService
) : GQLProjectEntityFieldContributor {

    override fun getFields(
            projectEntityClass: Class<out ProjectEntity>,
            projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
            if (projectEntityType == ProjectEntityType.PROJECT) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("gitCommitInfo")
                                .description("Information about a Git commit in the project")
                                .argument {
                                    it.name("commit")
                                            .description("Full or abbreviated hash of the commit to look for")
                                            .type(GraphQLNonNull(Scalars.GraphQLString))
                                }
                                .argument {
                                    it.name("first")
                                            .description("Flag to display only the first branch per type")
                                            .type(Scalars.GraphQLBoolean)
                                }
                                .type(ontrackGitCommitInfoGQLType.typeRef)
                                .dataFetcher { environment ->
                                    val project: Project = environment.getSource()
                                    val commit: String = environment.getArgument("commit")
                                    val first: Boolean? = environment.getArgument("first")
                                    val gitCommitInfo = gitService.getCommitProjectInfo(project.id, commit)
                                    if (first != null && first) {
                                           gitCommitInfo.first()
                                    } else {
                                        gitCommitInfo
                                    }
                                }
                                .build()
                )
            } else {
                null
            }


}