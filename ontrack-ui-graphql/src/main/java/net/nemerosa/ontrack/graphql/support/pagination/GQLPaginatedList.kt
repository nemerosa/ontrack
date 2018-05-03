package net.nemerosa.ontrack.graphql.support.pagination

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

/**
 * Creates a paginated GraphQL list type, linked to an actual
 * [PaginatedList] of objects.
 */
@Component
class GQLPaginatedList(
        private val pageInfo: GQLTypePageInfo
) {
    /**
     * Creates a paginated GraphQL list type, linked to an actual
     * [PaginatedList] of objects.
     *
     * @param itemType Type of item in the list
     */
    fun createPaginatedList(
            itemType: GQLType
    ): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name("${itemType.typeName}Paginated")
                .field {
                    it.name("pageInfo")
                            .description("Information about the current page")
                            .type(pageInfo.typeRef)
                }
                .field {
                    it.name("pageItems")
                            .description("Items in the current page")
                            .type(stdList(itemType.typeRef))
                }
                .build()
    }
}