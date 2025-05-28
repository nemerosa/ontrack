package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.security.AccountService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryAccountGroupByName(
    private val accountService: AccountService,
    private val accountGroup: GQLTypeAccountGroup
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
            .name("accountGroupByName")
            .type(accountGroup.typeRef)
            .argument(stringArgument(NAME_ARGUMENT, "Name of the group", nullable = false))
            .dataFetcher { env ->
                val name: String = env.getArgument(NAME_ARGUMENT)!!
                accountService.findAccountGroupByName(name)
            }
            .build()
    }

    companion object {
        const val NAME_ARGUMENT = "name"
    }
}
