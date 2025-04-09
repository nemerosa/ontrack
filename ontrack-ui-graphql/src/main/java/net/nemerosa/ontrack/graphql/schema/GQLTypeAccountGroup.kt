package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.descriptionField
import net.nemerosa.ontrack.graphql.support.idField
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.nameField
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

/**
 * @see AccountGroup
 */
@Component
class GQLTypeAccountGroup(
    private val accountService: AccountService,
    private val globalRole: GQLTypeGlobalRole,
    private val authorizedProject: GQLTypeAuthorizedProject,
    private val fieldContributors: List<GQLFieldContributor>,
) : GQLType {

    override fun getTypeName(): String = ACCOUNT_GROUP

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
            .name(ACCOUNT_GROUP)
            .field(idField())
            .field(nameField())
            .field(descriptionField())
            // Associated accounts
            .field { field ->
                field.name(ACCOUNTS_FIELD)
                    .description("List of associated accounts")
                    .type(listType(GraphQLTypeReference(GQLTypeAccount.ACCOUNT)))
                    .dataFetcher { env ->
                        val accountGroup: AccountGroup = env.getSource()!!
                        getAccountsForGroup(accountGroup)
                    }
            } // Global role
            .field { field ->
                field.name("globalRole")
                    .description("Global role for the account group")
                    .type(globalRole.typeRef)
                    .dataFetcher { env ->
                        val accountGroup: AccountGroup = env.getSource()!!
                        accountService.getGlobalRoleForAccountGroup(accountGroup).getOrNull()
                    }
            } // Authorised projects
            .field { field ->
                field.name("authorizedProjects")
                    .description("List of authorized projects")
                    .type(listType(authorizedProject.typeRef))
                    .dataFetcher { env ->
                        val accountGroup: AccountGroup = env.getSource()!!
                        accountService.getProjectPermissionsForAccountGroup(accountGroup)
                    }
            }
            // Links
            .fields(AccountGroup::class.java.graphQLFieldContributions(fieldContributors))
            // OK
            .build()
    }

    private fun getAccountsForGroup(accountGroup: AccountGroup): List<Account> {
        return accountService.getAccountsForGroup(accountGroup)
    }

    companion object {
        const val ACCOUNT_GROUP = "AccountGroup"
        const val ACCOUNTS_FIELD = "accounts"
    }

}