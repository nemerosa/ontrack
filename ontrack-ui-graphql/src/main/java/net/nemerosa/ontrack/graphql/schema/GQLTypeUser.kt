package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.listFieldGetter
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.objectField
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AuthenticatedUser
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class GQLTypeUser(
    private val securityService: SecurityService,
) : GQLType {

    companion object {
        const val USER_TYPE = "User"
    }

    override fun getTypeName(): String = USER_TYPE

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(USER_TYPE)
            .description("Representation of the current user")
            // Account representation
            .field(
                objectField("account", "Account associated to the user") {
                    securityService.currentUser?.account
                }
            )
            // Assigned groups
            .listFieldGetter<Any, AccountGroup>(
                name = "assignedGroups",
                description = "List of groups the user is assigned to",
            ) {
                securityService.currentUser?.assignedGroups ?: emptyList()
            }
            // Mapped groups
            .listFieldGetter<Any, AccountGroup>(
                name = "mappedGroups",
                description = "List of groups that have been mapped to the user from their IdP groups",
            ) {
                securityService.currentUser?.mappedGroups ?: emptyList()
            }
            // IdP groups
            .field {
                it.name(AuthenticatedUser::idpGroups.name)
                    .description(getPropertyDescription(AuthenticatedUser::idpGroups))
                    .type(listType(GraphQLString))
                    .dataFetcher {
                        securityService.currentUser?.idpGroups ?: emptyList<String>()
                    }
            }
            // OK
            .build()

}

/**
 * Pseudo item associated to the root user type
 */
class RootUser private constructor() {
    companion object {
        val INSTANCE = RootUser()
    }
}