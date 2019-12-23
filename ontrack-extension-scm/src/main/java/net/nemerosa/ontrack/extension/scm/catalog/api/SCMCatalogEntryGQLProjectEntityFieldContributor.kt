package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class SCMCatalogEntryGQLProjectEntityFieldContributor(
        private val catalogInfo: GQLTypeCatalogInfo
) : GQLProjectEntityFieldContributor {

    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>? =
            if (projectEntityType == ProjectEntityType.PROJECT) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("scmCatalogEntryLink")
                                .description("Link to a SCM catalog entry")
                                .type(catalogInfo.typeRef)
                                .dataFetcher { env -> loadCatalogInfo(env) }
                                .build()
                )
            } else {
                null
            }

    private fun loadCatalogInfo(env: DataFetchingEnvironment): GQLTypeCatalogInfo.Data {
        val project: Project = env.getSource()
        return GQLTypeCatalogInfo.Data(project)
    }

}