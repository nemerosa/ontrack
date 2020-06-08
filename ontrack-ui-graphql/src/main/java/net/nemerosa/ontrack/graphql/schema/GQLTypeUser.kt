package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.objectField
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class GQLTypeUser(
        private val securityService: SecurityService
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
                            objectField<Account>("account", "Account associated to the user") {
                                securityService.currentAccount?.account
                            }
                    )
                    // OK
                    .build()

    /**
     * Pseudo item associated to the user type
     */
    class Data

}
