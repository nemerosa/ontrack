package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.stereotype.Component

/**
 * Field to launch a search, returning paginated results.
 */
@Component
class GQLRootQuerySearch(
        private val searchResult: GQLTypeSearchResult,
        private val paginatedListFactory: GQLPaginatedListFactory,
        private val searchService: SearchService
) : GQLRootQuery {

    companion object {
        private const val ARG_SEARCH_TOKEN = "token"
        private const val ARG_SEARCH_TYPE = "type"
    }

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return paginatedListFactory.createPaginatedField(
                cache = GQLTypeCache(),
                fieldName = "search",
                fieldDescription = "Performs a search in Ontrack",
                itemType = searchResult,
                arguments = listOf(
                        GraphQLArgument.newArgument().name(ARG_SEARCH_TOKEN).description("Query string").type(GraphQLNonNull(GraphQLString)).build(),
                        GraphQLArgument.newArgument().name(ARG_SEARCH_TYPE).description("Result type").type(GraphQLString).build()
                ),
                itemPaginatedListProvider = { env, _: Any?, offset, size ->
                    val token: String = env.getArgument(ARG_SEARCH_TOKEN)
                    val type: String? = env.getArgument(ARG_SEARCH_TYPE)
                    val request = SearchRequest(token, type, offset, size)
                    val results = searchService.paginatedSearch(request)
                    PaginatedList.create(
                            items = results.items,
                            offset = offset,
                            pageSize = size,
                            total = results.total
                    )
                }
        )
    }

}