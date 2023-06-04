package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.GlobalRole
import org.springframework.stereotype.Component

/**
 * @see net.nemerosa.ontrack.model.security.GlobalRole
 */
@Component
class GQLTypeGlobalRole(
        private val accountService: AccountService,
) : GQLType {

    override fun getTypeName(): String = GLOBAL_ROLE

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(GLOBAL_ROLE)
                .stringField(GlobalRole::id)
                .stringField(GlobalRole::name)
                .stringField(GlobalRole::description)
                .field {
                    it.name("groups")
                            .description("List of groups having this role")
                            .type(listType(GQLTypeAccountGroup.ACCOUNT_GROUP))
                            .dataFetcher { env ->
                                val globalRole: GlobalRole = env.getSource()
                                accountService.findAccountGroupsByGlobalRole(globalRole)
                            }
                }
                .field {
                    it.name("accounts")
                            .description("List of accounts having this role")
                            .type(listType(GraphQLTypeReference(GQLTypeAccount.ACCOUNT)))
                            .dataFetcher { env ->
                                val globalRole: GlobalRole = env.getSource()
                                accountService.findAccountsByGlobalRole(globalRole)
                            }
                }
                // OK
                .build()
    }

    companion object {
        const val GLOBAL_ROLE = "GlobalRole"
    }
}
