package net.nemerosa.ontrack.graphql.schema

import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.ProjectRole
import net.nemerosa.ontrack.model.security.RolesService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLProjectAuthorizationsFieldContributor(
        private val accountService: AccountService,
        private val rolesService: RolesService,
        private val projectAuthorization: GQLTypeProjectAuthorization
) : GQLProjectEntityFieldContributor {

    override fun getFields(
            projectEntityClass: Class<out ProjectEntity>,
            projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? {
        return if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf<GraphQLFieldDefinition>(
                    GraphQLFieldDefinition.newFieldDefinition()
                            .name("projectRoles")
                            .description("Authorisations for the project")
                            .type(listType(projectAuthorization.typeRef))
                            .argument(stringArgument("role", "Filter by role name"))
                            .dataFetcher(projectAuthorizationsFetcher())
                            .build()
            )
        } else {
            emptyList<GraphQLFieldDefinition>()
        }
    }

    private fun projectAuthorizationsFetcher() = DataFetcher { env ->
        val project: Project = env.getSource()
        val role: String? = env.getArgument("role")
        rolesService.projectRoles
                .filter { projectRole ->
                    role?.takeIf { it.isNotBlank() }?.let {
                        it == projectRole.id
                    } ?: true
                }
                .map { projectRole ->
                    getProjectAuthorizations(project, projectRole)
                }
    }

    private fun getProjectAuthorizations(project: Project, projectRole: ProjectRole): GQLTypeProjectAuthorization.Model {
        return GQLTypeProjectAuthorization.Model(
                projectRole.id,
                projectRole.name,
                projectRole.description,
                accountService.findAccountGroupsByProjectRole(project, projectRole),
                accountService.findAccountsByProjectRole(project, projectRole)
        )
    }
}
