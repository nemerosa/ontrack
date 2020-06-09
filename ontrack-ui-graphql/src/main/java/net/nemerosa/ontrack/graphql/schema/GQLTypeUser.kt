package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.authorizations.*
import net.nemerosa.ontrack.graphql.support.objectField
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GQLTypeUser(
        private val securityService: SecurityService,
        private val authorizationsService: AuthorizationsService
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
                                securityService.currentAccount?.account
                            }
                    )
                    // Authorizations
                    .authorizations(authorizationsService, RootUser::class)
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

/**
 * Root authorizations
 */
@Component
class RootUserAuthorizations(
        private val securityService: SecurityService
) : Authorizations<RootUser> {

    override val targetType: KClass<RootUser> = RootUser::class

    override val authorizations: List<Authorization<RootUser>> = listOf(
            Authorization("createProject", "Creating a project") {
                securityService.isGlobalFunctionGranted<ProjectCreation>()
            }
    )

}