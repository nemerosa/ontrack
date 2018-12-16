package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GitCommitInfoProjectGraphQLFieldContributor : GQLProjectEntityFieldContributor {

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
                                // TODO OntrackGitCommitInfo type
                                // TODO Fetcher
                                .build()
                )
            } else {
                null
            }


}