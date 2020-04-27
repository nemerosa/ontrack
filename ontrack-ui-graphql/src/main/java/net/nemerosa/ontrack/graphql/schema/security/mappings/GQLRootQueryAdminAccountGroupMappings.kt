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
                    a.name(MAPPING_TYPE_ARGUMENT)
                            .description("Mapping type")
                            .type(GraphQLString)
                }
                .argument { a: GraphQLArgument.Builder ->
                    a.name(MAPPING_NAME_ARGUMENT)
                            .description("Mapping name")
                            .type(GraphQLString)
                }
                .argument { a: GraphQLArgument.Builder ->
                    a.name(MAPPING_GROUP_ARGUMENT)
                            .description("Group name")
                            .type(GraphQLString)
                }
                .dataFetcher(adminAccountGroupMappingsFetcher())
                .build()
    }

    private fun adminAccountGroupMappingsFetcher() = DataFetcher { environment: DataFetchingEnvironment ->
        var filter = { _: AccountGroupMapping -> true }
        // Filter on name
        val nameArgument: String? = environment.getArgument<String>(MAPPING_NAME_ARGUMENT)
        if (nameArgument != null) {
            filter = filter and { agm: AccountGroupMapping -> agm.name.contains(nameArgument, ignoreCase = true) }
        }
        // Filter on group
        val groupArgument: String? = environment.getArgument<String>(MAPPING_GROUP_ARGUMENT)
        if (groupArgument != null) {
            filter = filter.and { agm: AccountGroupMapping -> agm.group.name.contains(groupArgument, ignoreCase = true) }
        }
        // Filter on type
        val typeArgument: String? = environment.getArgument<String>(MAPPING_TYPE_ARGUMENT)
        // Getting the unfiltered list
        val unfiltered = if (typeArgument != null) {
            accountGroupMappingService.getMappings(typeArgument)
        } else {
            accountGroupMappingService.mappings
        }
        // Filtering
        unfiltered.filter(filter)
    }

    companion object {
        private const val MAPPING_TYPE_ARGUMENT = "type"
        private const val MAPPING_NAME_ARGUMENT = "name"
        private const val MAPPING_GROUP_ARGUMENT = "group"
    }

}