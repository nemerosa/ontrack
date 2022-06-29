package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

/**
 * Adds a `gitRepositoryHtmlURL` field at project level containing the URL to the HTML page of the Git repository
 * if available.
 */
@Component
class GitRepositoryHtmlURLProjectGraphQLFieldContributor(
    private val scmDetector: SCMDetector,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("gitRepositoryHtmlURL")
                    .description("URL to the HTML page of the Git repository associated with this project")
                    .type(GraphQLString)
                    .dataFetcher { env ->
                        val project: Project = env.getSource()
                        scmDetector.getSCM(project)?.repositoryHtmlURL
                    }
                    .build()
            )
        } else {
            null
        }

}