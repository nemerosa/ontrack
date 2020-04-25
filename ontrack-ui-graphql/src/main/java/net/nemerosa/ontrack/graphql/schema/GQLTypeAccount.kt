package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class GQLTypeAccount(
        private val accountService: AccountService,
        private val globalRole: GQLTypeGlobalRole,
        private val authorizedProject: GQLTypeAuthorizedProject
) : GQLType {

    override fun getTypeName(): String = ACCOUNT

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(ACCOUNT)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField("Unique name for the account"))
                .field(GraphqlUtils.stringField("fullName", "Full name of the account"))
                .field(GraphqlUtils.stringField("email", "Email of the account"))
                .field {
                    it.name("authenticationSource")
                            .description("Source of authentication (builtin, ldap, etc.)")
                            .type(Scalars.GraphQLString)
                            .dataFetcher { environment: DataFetchingEnvironment -> (environment.getSource<Any>() as Account).authenticationSource.id }
                }
                .field(GraphqlUtils.stringField("role", "Security role (admin or none)"))
                .field {
                    it.name("groups")
                            .description("List of groups the account belongs to")
                            .type(GraphqlUtils.stdList(GraphQLTypeReference(GQLTypeAccountGroup.ACCOUNT_GROUP)))
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
                            .type(GraphqlUtils.stdList(authorizedProject.typeRef))
                            .dataFetcher(accountAuthorizedProjectsFetcher())
                }
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