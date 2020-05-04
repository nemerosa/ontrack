package net.nemerosa.ontrack.graphql.schema.security.mappings

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeAccountGroupMapping
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.security.AccountGroupMapping
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryAdminAccountGroupMappings(
        private val accountGroupMappingService: AccountGroupMappingService,
        private val accountGroupMapping: GQLTypeAccountGroupMapping
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("accountGroupMappings")
                .type(GraphqlUtils.stdList(accountGroupMapping.typeRef))
                .argument { a: GraphQLArgument.Builder ->
                    a.name(ARG_PROVIDER)
                            .description("Authentication source provider")
                            .type(GraphQLString)
                }
                .argument { a: GraphQLArgument.Builder ->
                    a.name(ARG_SOURCE)
                            .description("Authentication source name")
                            .type(GraphQLString)
                }
                .argument { a: GraphQLArgument.Builder ->
                    a.name(ARG_NAME)
                            .description("Mapping name")
                            .type(GraphQLString)
                }
                .argument { a: GraphQLArgument.Builder ->
                    a.name(ARG_GROUP)
                            .description("Group name")
                            .type(GraphQLString)
                }
                .dataFetcher(adminAccountGroupMappingsFetcher())
                .build()
    }

    private fun adminAccountGroupMappingsFetcher() = DataFetcher { environment: DataFetchingEnvironment ->
        var filter = { _: AccountGroupMapping -> true }
        // Filter on name
        val nameArgument: String? = environment.getArgument<String>(ARG_NAME)
        if (nameArgument != null && nameArgument.isNotBlank()) {
            filter = filter and { agm: AccountGroupMapping -> agm.name.contains(nameArgument, ignoreCase = true) }
        }
        // Filter on group
        val groupArgument: String? = environment.getArgument<String>(ARG_GROUP)
        if (groupArgument != null && groupArgument.isNotBlank()) {
            filter = filter.and { agm: AccountGroupMapping -> agm.group.name.contains(groupArgument, ignoreCase = true) }
        }
        // Filter on provider
        val providerArgument: String? = environment.getArgument<String>(ARG_PROVIDER)
        if (providerArgument != null && providerArgument.isNotBlank()) {
            filter = filter and { agm: AccountGroupMapping -> agm.authenticationSource.provider.equals(providerArgument, ignoreCase = true) }
        }
        // Filter on source
        val sourceArgument: String? = environment.getArgument<String>(ARG_SOURCE)
        if (sourceArgument != null && sourceArgument.isNotBlank()) {
            filter = filter and { agm: AccountGroupMapping -> agm.authenticationSource.key.equals(sourceArgument, ignoreCase = true) }
        }
        // Getting the filtered list
        accountGroupMappingService.mappings.filter(filter)
    }

    companion object {
        private const val ARG_PROVIDER = "provider"
        private const val ARG_SOURCE = "source"
        private const val ARG_NAME = "name"
        private const val ARG_GROUP = "group"
    }

}