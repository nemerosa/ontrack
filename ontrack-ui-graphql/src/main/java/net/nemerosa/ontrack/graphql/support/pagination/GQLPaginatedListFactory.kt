package net.nemerosa.ontrack.graphql.support.pagination

import graphql.Scalars.GraphQLInt
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.pagination.PageRequest
import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.stereotype.Component

/**
 * Creates a paginated GraphQL list type, linked to an actual
 * [PaginatedList] of objects.
 */
@Component
class GQLPaginatedListFactory(
        private val pageInfo: GQLTypePageInfo
) {

    companion object {
        const val ARG_OFFSET = "offset"
        const val ARG_SIZE = "size"
    }

    fun <P, T> createPaginatedField(
            fieldName: String,
            fieldDescription: String,
            itemType: GQLType,
            itemListCounter: (DataFetchingEnvironment, P) -> Int,
            itemListProvider: (DataFetchingEnvironment, P, Int, Int) -> List<T>,
            arguments: List<GraphQLArgument> = emptyList()
    ): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(fieldName)
                .description(fieldDescription)
                .argument {
                    it.name(ARG_OFFSET)
                            .description("Offset for the page")
                            .type(GraphQLInt)
                            .defaultValue(0)
                }
                .argument {
                    it.name(ARG_SIZE)
                            .description("Size of the page")
                            .type(GraphQLInt)
                            .defaultValue(PageRequest.DEFAULT_PAGE_SIZE)
                }
                .argument(arguments)
                .type(createPaginatedList(itemType))
                .dataFetcher { environment ->
                    val source: P = environment.getSource<P>()
                    val offset = environment.getArgument<Int>(ARG_OFFSET) ?: 0
                    val size = environment.getArgument<Int>(ARG_SIZE) ?: PageRequest.DEFAULT_PAGE_SIZE
                    val total = itemListCounter(environment, source)
                    val items = itemListProvider(
                            environment,
                            source,
                            offset,
                            size
                    )
                    PaginatedList.create(
                            items = items,
                            offset = offset,
                            pageSize = size,
                            total = total)
                }
                .build()
    }

    /**
     * Creates a paginated GraphQL list type, linked to an actual
     * [PaginatedList] of objects.
     *
     * @param itemType Type of item in the list
     */
    private fun createPaginatedList(
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