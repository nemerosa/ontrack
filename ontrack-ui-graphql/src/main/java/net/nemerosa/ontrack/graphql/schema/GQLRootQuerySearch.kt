package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.stereotype.Component

/**
 * Field to launch a search, returning paginated results.
 */
@Component
class GQLRootQuerySearch(
        private val searchResult: GQLTypeSearchResult,
        private val searchService: SearchService
) : GQLRootQuery {

    companion object {
        private const val ARG_SEARCH_TOKEN = "token"
        private const val ARG_SEARCH_TYPE = "type"
    }

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                // Name & description
                .name("search").description("Performs a search in Ontrack")
                // Search arguments
                .argument {
                    it.name(ARG_SEARCH_TOKEN).description("Query string").type(GraphQLNonNull(GraphQLString))
                }
                .argument {
                    it.name(ARG_SEARCH_TYPE).description("Result type").type(GraphQLString)
                }
                // Output
                // TODO Paginated results
                .type(stdList(searchResult.typeRef))
                // Getting the results
                .dataFetcher { env ->
                    val token: String = env.getArgument(ARG_SEARCH_TOKEN)
                    val type: String? = env.getArgument(ARG_SEARCH_TYPE)
                    searchService.search(SearchRequest(token, type))
                }
                // OK
                .build()
    }

}