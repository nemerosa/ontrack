package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringArgument
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
        return paginatedListFactory.createRootPaginatedField(
                cache = GQLTypeCache(),
                fieldName = "search",
                fieldDescription = "Performs a search in Ontrack",
                itemType = searchResult.typeName,
                arguments = listOf(
                    stringArgument(ARG_SEARCH_TOKEN, "Query string", nullable = false),
                    stringArgument(ARG_SEARCH_TYPE, "Result type", nullable = false),
                ),
                itemPaginatedListProvider = { env, offset, size ->
                    val token: String = env.getArgument(ARG_SEARCH_TOKEN)!!
                    val type: String = env.getArgument(ARG_SEARCH_TYPE)!!
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