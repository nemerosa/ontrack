package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetcher
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.security.GQLTypeAuthenticationSource
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.idField
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.stereotype.Component

@Component
class GQLTypeAccount(
    private val accountService: AccountService,
    private val securityService: SecurityService,
    private val tokensService: TokensService,
    private val globalRole: GQLTypeGlobalRole,
    private val authorizedProject: GQLTypeAuthorizedProject,
    private val token: GQLTypeToken,
    private val fieldContributors: List<GQLFieldContributor>,
    private val authenticationSource: GQLTypeAuthenticationSource,
    private val accountGroupContributors: List<AccountGroupContributor>,
    private val providedGroupsService: ProvidedGroupsService,
) : GQLType {

    override fun getTypeName(): String = ACCOUNT

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
            .name(ACCOUNT)
            .field(idField())
            .stringField(Account::name)
            .stringField(Account::fullName, "Full name of the account")
            .stringField(Account::email, "Email of the account")
            .field {
                it.name("authenticationSource")
                    .description("Source of authentication (builtin, ldap, etc.)")
                    .deprecate("Will be removed in V5.")
                    .type(authenticationSource.typeRef)
            }
            .stringField(Account::role.name, "Security role (admin or none)")
            .field {
                it.name("groups")
                    .description("List of groups the account belongs to")
                    .deprecate("Will be removed in V5. Use the User type fields.")
                    .type(listType(GraphQLTypeReference(GQLTypeAccountGroup.ACCOUNT_GROUP)))
                    .dataFetcher(accountAccountGroupsFetcher())
            }
            .field {
                it.name("globalRole")
                    .description("Global role for the account")
                    .type(globalRole.typeRef)
                    .dataFetcher(accountGlobalRoleFetcher())
            }
            .field {
                it.name("authorizedProjects")
                    .description("List of authorized projects")
                    .type(listType(authorizedProject.typeRef))
                    .dataFetcher(accountAuthorizedProjectsFetcher())
            }
            .field {
                it.name("token")
                    .deprecate("Use the list of tokens. Will be removed in V5.")
                    .description("Authentication token, if any, linked to this account.")
                    .type(token.typeRef)
                    .dataFetcher { env ->
                        val account: Account = env.getSource()
                        securityService.asAdmin {
                            tokensService.getToken(account.id())
                        }
                    }
            }
            .field {
                it.name("tokens")
                    .description("List of authentication tokens linked to this account.")
                    .type(listType(token.typeRef))
                    .dataFetcher { env ->
                        val account: Account = env.getSource()
                        securityService.asAdmin {
                            tokensService.getTokens(account.id()).map { t ->
                                t.obfuscate()
                            }
                        }
                    }
            }
            .booleanField(Account::disabled)
            .booleanField(Account::locked)
            // Contributed groups
            .field {
                it.name("contributedGroups")
                    .description("List of groups contributed to this account. Some groups are available only after the user has logged in.")
                    .deprecate("Will be removed in V5. Use the User type fields.")
                    .type(listType(GraphQLTypeReference(GQLTypeAccountGroup.ACCOUNT_GROUP)))
                    .dataFetcher { env ->
                        val account: Account = env.getSource()
                        securityService.asAdmin {
                            accountGroupContributors.flatMap { contributor -> contributor.collectGroups(account) }
                        }
                    }
            }
            // Provided groups
            .field {
                it.name("providedGroups")
                    .description("List of groups provided to this account. Some groups are available only after the user has logged in.")
                    .deprecate("Will be removed in V5. Use the User type fields.")
                    .type(listType(GraphQLString))
                    .dataFetcher { env ->
                        val account: Account = env.getSource()
                        securityService.asAdmin {
                            providedGroupsService.getProvidedGroups(account.id(), account.authenticationSource).toList()
                                .sorted()
                        }
                    }
            }
            // Links
            .fields(Account::class.java.graphQLFieldContributions(fieldContributors))
            // OK
            .build()
    }

    private fun accountAuthorizedProjectsFetcher() = DataFetcher<Collection<ProjectRoleAssociation>> { env ->
        val account: Account = env.getSource()
        accountService.getProjectPermissionsForAccount(account)
    }

    private fun accountGlobalRoleFetcher() = DataFetcher { env ->
        val account: Account = env.getSource()
        accountService.getGlobalRoleForAccount(account).orElse(null)
    }

    private fun accountAccountGroupsFetcher() = DataFetcher<List<AccountGroup>> { env ->
        val id = env.getSource<Account>().id
        accountService.getGroupsForAccount(id)
    }

    companion object {
        const val ACCOUNT = "Account"
    }

}