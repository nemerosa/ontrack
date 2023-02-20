package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.service.SCMFileChangeFilterService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class SCMFileChangeFiltersProjectGraphQLFieldContributor(
    private val gqlTypeSCMFileChangeFilter: GQLTypeSCMFileChangeFilter,
    private val scmFileChangeFilterService: SCMFileChangeFilterService,
): GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("scmFileChangeFilters")
                    .description("List of saved SCM file filters for the project")
                    .type(listType(gqlTypeSCMFileChangeFilter.typeRef))
                    .dataFetcher { env ->
                        val project: Project = env.getSource()
                        scmFileChangeFilterService.loadSCMFileChangeFilters(project).filters
                    }
                    .build()
            )
        } else {
            null
        }
}