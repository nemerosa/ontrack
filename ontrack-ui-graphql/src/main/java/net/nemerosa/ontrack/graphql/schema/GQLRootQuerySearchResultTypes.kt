package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySearchResultTypes(
        private val searchResultType: GQLTypeSearchResultType,
        private val searchService: SearchService
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("searchResultTypes")
                .description("List of types of search results")
                .type(stdList(searchResultType.typeRef))
                .dataFetcher { searchService.searchResultTypes }
                .build()
    }
}