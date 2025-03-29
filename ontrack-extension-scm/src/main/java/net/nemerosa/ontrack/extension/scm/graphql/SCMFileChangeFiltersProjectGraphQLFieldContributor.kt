package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.service.SCMFileChangeFilterService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class SCMFileChangeFiltersProjectGraphQLFieldContributor(
    private val gqlTypeSCMFileChangeFilters: GQLTypeSCMFileChangeFilters,
    private val scmFileChangeFilterService: SCMFileChangeFilterService,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("scmFileChangeFilters")
                    .description("List of saved SCM file filters for the project")
                    .type(gqlTypeSCMFileChangeFilters.typeRef.toNotNull())
                    .dataFetcher { env ->
                        val project: Project = env.getSource()!!
                        GQLTypeSCMFileChangeFilters.SCMFileChangeFiltersWithProject(
                            project = project,
                            filters = scmFileChangeFilterService.loadSCMFileChangeFilters(project),
                        )
                    }
                    .build()
            )
        } else {
            null
        }
}