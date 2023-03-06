package net.nemerosa.ontrack.extension.scm.graphql

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilters
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.annotations.getPropertyName
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMFileChangeFilters(
    private val gqlTypeSCMFileChangeFilter: GQLTypeSCMFileChangeFilter,
    private val securityService: SecurityService,
) : GQLType {

    override fun getTypeName(): String = SCMFileChangeFilters::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of file change filters")
            // Actual list of filters
            .field {
                it.name(getPropertyName(SCMFileChangeFilters::filters))
                    .description("List of filters")
                    .type(listType(gqlTypeSCMFileChangeFilter.typeRef))
                    .dataFetcher { env ->
                        env.getSource<SCMFileChangeFiltersWithProject>().filters.filters
                    }
            }
            // Flag indicating if filters can be shared by the current user
            .field {
                it.name("canManage")
                    .description("True if the user can manage shared filters")
                    .type(GraphQLBoolean.toNotNull())
                    .dataFetcher { env ->
                        val project = env.getSource<SCMFileChangeFiltersWithProject>().project
                        securityService.isProjectFunctionGranted(project, ProjectConfig::class.java)
                    }
            }
            // OK
            .build()

    data class SCMFileChangeFiltersWithProject(
        val project: Project,
        val filters: SCMFileChangeFilters,
    )
}