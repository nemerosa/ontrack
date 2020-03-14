package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class SCMCatalogEntryGQLProjectEntityFieldContributor(
        private val catalogLinkService: CatalogLinkService,
        private val scmCatalogEntry: GQLTypeSCMCatalogEntry
) : GQLProjectEntityFieldContributor {

    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>? =
            if (projectEntityType == ProjectEntityType.PROJECT) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("scmCatalogEntry")
                                .description("SCM catalog entry the project is linked with, if any")
                                .type(scmCatalogEntry.typeRef)
                                .dataFetcher { env ->
                                    val project: Project = env.getSource()
                                    catalogLinkService.getSCMCatalogEntry(project)
                                }
                                .build()
                )
            } else {
                null
            }

}