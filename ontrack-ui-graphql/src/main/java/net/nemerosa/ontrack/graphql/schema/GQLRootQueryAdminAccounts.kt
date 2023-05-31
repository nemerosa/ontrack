package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import org.springframework.stereotype.Component

@Component
class GQLRootQueryAdminAccounts(
        private val accountService: AccountService,
        private val account: GQLTypeAccount,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("accounts")
                .type(listType(account.typeRef, nullable = true, nullableItem = false))
                .argument(
                        GraphQLArgument.newArgument()
                                .name(ID_ARGUMENT)
                                .description("Searching by ID")
                                .type(Scalars.GraphQLInt)
                                .build()
                )
                .argument(
                        GraphQLArgument.newArgument()
                                .name(NAME_ARGUMENT)
                                .description("Searching by looking for a string in the name or the full name")
                                .type(Scalars.GraphQLString)
                                .build()
                )
                .argument(
                        GraphQLArgument.newArgument()
                                .name(GROUP_ARGUMENT)
                                .description("Searching by looking for a string in one of the groups the account belongs to")
                                .type(Scalars.GraphQLString)
                                .build()
                )
                .dataFetcher(adminAccountsFetcher())
                .build()
    }

    private fun adminAccountsFetcher(): DataFetcher<*> = DataFetcher { environment: DataFetchingEnvironment ->
        val id = environment.getArgument<Int>(ID_ARGUMENT)
        val name = environment.getArgument<String>(NAME_ARGUMENT)
        val group = environment.getArgument<String>(GROUP_ARGUMENT)
        if (id != null) {
            listOf<Account>(
                    accountService.getAccount(of(id))
            )
        } else {
            var filter: (Account) -> Boolean = { true }
            // Filter by name
            if (!name.isNullOrBlank()) {
                filter = filter and { account ->
                    account.name.contains(name, ignoreCase = true) ||
                            account.fullName.contains(name, ignoreCase = true)

                }
            }
            // Filter by group
            if (!group.isNullOrBlank()) {
                filter = filter and { account ->
                    accountService.getGroupsForAccount(account.id).any { accountGroup ->
                        accountGroup.name.contains(group, ignoreCase = true)
                    }
                }
            }
            // Getting the list
            accountService.accounts.filter(filter)
        }
    }

    companion object {
        const val ID_ARGUMENT = "id"
        const val NAME_ARGUMENT = "name"
        const val GROUP_ARGUMENT = "group"
    }
}
