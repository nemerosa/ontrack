package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import org.springframework.stereotype.Component

@Component
class GQLRootQueryAdminAccountGroups(
        private val accountService: AccountService,
        private val accountGroup: GQLTypeAccountGroup
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("accountGroups")
                .type(listType(accountGroup.typeRef, nullable = true))
                .argument { arg: GraphQLArgument.Builder ->
                    arg.name(ID_ARGUMENT)
                            .description("Searching by ID")
                            .type(Scalars.GraphQLInt)
                }
                .argument { arg: GraphQLArgument.Builder ->
                    arg.name(NAME_ARGUMENT)
                            .description("Searching by looking for a string in the name or the description")
                            .type(Scalars.GraphQLString)
                }
                .dataFetcher(adminAccountGroupsFetcher())
                .build()
    }

    private fun adminAccountGroupsFetcher(): DataFetcher<*> {
        return DataFetcher { environment: DataFetchingEnvironment ->
            val id = environment.getArgument<Int>(ID_ARGUMENT)
            val name = environment.getArgument<String>(NAME_ARGUMENT)
            if (id != null) {
                return@DataFetcher listOf<AccountGroup>(
                        accountService.getAccountGroup(of(id))
                )
            } else {
                var filter: (AccountGroup) -> Boolean = { true }
                // Filter by name
                if (!name.isNullOrBlank()) {
                    filter = filter and { group ->
                        group.name.contains(name, ignoreCase = true) ||
                                group.description?.contains(name, ignoreCase = true) ?: true
                    }
                }
                // Getting the list
                accountService.accountGroups.filter(filter)
            }
        }
    }

    companion object {
        const val ID_ARGUMENT = "id"
        const val NAME_ARGUMENT = "name"
    }
}
