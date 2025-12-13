package net.nemerosa.ontrack.graphql.schema.security

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.security.AccountService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGlobalPermissions(
    private val gqlTypeGlobalPermission: GQLTypeGlobalPermission,
    private val accountService: AccountService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("globalPermissions")
            .description("List of global permissions")
            .type(listType(gqlTypeGlobalPermission.typeRef))
            .dataFetcher {
                accountService.globalPermissions
            }
            .build()
}