package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import net.nemerosa.ontrack.model.security.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @see AccountGroup
 */
@Component
class GQLTypeAccountGroup(private val accountService: AccountService,
                          private val accountGroupMappingService: AccountGroupMappingService,
                          private val globalRole: GQLTypeGlobalRole,
                          private val authorizedProject: GQLTypeAuthorizedProject,
                          private val accountGroupMapping: GQLTypeAccountGroupMapping,
                          private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun getTypeName(): String = ACCOUNT_GROUP

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(ACCOUNT_GROUP)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField())
                .field(GraphqlUtils.descriptionField())
                .booleanField(AccountGroup::autoJoin)
                // Associated accounts
                .field { field: GraphQLFieldDefinition.Builder ->
                    field.name(ACCOUNTS_FIELD)
                            .description("List of associated accounts")
                            .type(GraphqlUtils.stdList(GraphQLTypeReference(GQLTypeAccount.ACCOUNT)))
                            .dataFetcher(GraphqlUtils.fetcher(AccountGroup::class.java) { accountGroup: AccountGroup -> getAccountsForGroup(accountGroup) })
                } // Global role
                .field { field: GraphQLFieldDefinition.Builder ->
                    field.name("globalRole")
                            .description("Global role for the account group")
                            .type(globalRole.typeRef)
                            .dataFetcher(GraphqlUtils.fetcher(
                                    AccountGroup::class.java
                            ) { group: AccountGroup? -> accountService.getGlobalRoleForAccountGroup(group).orElse(null) })
                } // Authorised projects
                .field { field: GraphQLFieldDefinition.Builder ->
                    field.name("authorizedProjects")
                            .description("List of authorized projects")
                            .type(GraphqlUtils.stdList(authorizedProject.typeRef))
                            .dataFetcher(GraphqlUtils.fetcher(
                                    AccountGroup::class.java) { group: AccountGroup? -> accountService.getProjectPermissionsForAccountGroup(group) })
                } // Mappings
                .field { field: GraphQLFieldDefinition.Builder ->
                    field.name("mappings")
                            .description("Mappings for this group")
                            .type(GraphqlUtils.stdList(accountGroupMapping.typeRef))
                            .dataFetcher(GraphqlUtils.fetcher(
                                    AccountGroup::class.java) { group: AccountGroup? -> accountGroupMappingService.getMappingsForGroup(group!!) })
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