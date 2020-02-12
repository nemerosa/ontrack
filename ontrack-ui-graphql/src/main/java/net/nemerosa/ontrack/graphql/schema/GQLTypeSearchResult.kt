package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLFloat
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.model.structure.SearchResult
import org.springframework.stereotype.Component

/**
 * Type for [SearchResult].
 */
@Component
class GQLTypeSearchResult(
        private val searchResultType: GQLTypeSearchResultType
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(typeName)
                .description("Search result")
                .field {
                    it.name("title")
                            .description("Short title")
                            .type(GraphQLString)
                }
                .field {
                    it.name("description")
                            .description("Description linked to the item being found")
                            .type(GraphQLString)
                }
                .field {
                    it.name("uri")
                            .description("API access point")
                            .type(GraphQLString)
                }
                .field {
                    it.name("page")
                            .description("Web access point")
                            .type(GraphQLString)
                }
                .field {
                    it.name("accuracy")
                            .description("Score for the search")
                            .type(GraphQLFloat)
                }
                .field {
                    it.name("type")
                            .description("Type of result")
                            .type(searchResultType.typeRef)
                }
                .build()
    }

    override fun getTypeName(): String = SearchResult::class.java.simpleName
}
