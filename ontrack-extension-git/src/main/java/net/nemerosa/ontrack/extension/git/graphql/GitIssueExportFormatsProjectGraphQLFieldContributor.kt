package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.graphql.GQLTypeExportFormat
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GitIssueExportFormatsProjectGraphQLFieldContributor(
    private val gqlTypeExportFormat: GQLTypeExportFormat,
    private val gitService: GitService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("gitIssueExportFormats")
                    .description("List of formats supported for the export of issues in this project")
                    .type(listType(gqlTypeExportFormat.typeRef))
                    .dataFetcher { env ->
                        val project: Project = env.getSource()
                        gitService.getIssueExportFormats(project)
                    }
                    .build()
            )
        } else {
            null
        }


}