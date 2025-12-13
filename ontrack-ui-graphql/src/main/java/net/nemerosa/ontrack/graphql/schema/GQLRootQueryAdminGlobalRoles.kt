package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.security.RolesService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GQLRootQueryAdminGlobalRoles(
        private val globalRole: GQLTypeGlobalRole,
        private val rolesService: RolesService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("globalRoles")
                    .description("List of global security roles")
                    .type(listType(globalRole.typeRef))
                    .argument(stringArgument("role", "Filter by role name"))
                    .dataFetcher { env ->
                        val role: String? = env.getArgument("role")
                        role?.takeIf { it.isNotBlank() }?.let {
                            listOfNotNull(
                                    rolesService.getGlobalRole(it).getOrNull()
                            )
                        } ?: rolesService.globalRoles
                    }
                    .build()
}
