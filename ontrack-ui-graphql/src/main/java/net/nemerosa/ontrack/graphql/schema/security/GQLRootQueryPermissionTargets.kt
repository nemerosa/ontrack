package net.nemerosa.ontrack.graphql.schema.security

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.PermissionTarget
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPermissionTargets(
    private val accountService: AccountService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("permissionTargets")
            .description("List of permission targets")
            .type(listType(PermissionTarget::class.java.simpleName))
            .argument(stringArgument(ARG_NAME, "Part of the name of the permission target to look for"))
            .dataFetcher { env ->
                val name: String? = env.getArgument(ARG_NAME)
                accountService.searchPermissionTargets(name)
            }
            .build()

    companion object {
        private const val ARG_NAME = "token"
    }
}