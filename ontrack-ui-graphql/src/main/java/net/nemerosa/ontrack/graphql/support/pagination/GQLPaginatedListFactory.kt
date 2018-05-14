package net.nemerosa.ontrack.graphql.support.pagination

import graphql.Scalars.GraphQLInt
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
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

    /**
     * Creates a paginated field from a [PaginatedList] provider.
     *
     * @param fieldName Name of the field
     * @param fieldDescription Description of the field
     * @param itemType Type of the items being paginated
     * @param itemPaginatedListProvider Function to provide the paginated list directly.
     * @param arguments Optional list of arguments to add to the field
     */
    fun <P, T> createPaginatedField(
            cache: GQLTypeCache,
            fieldName: String,
            fieldDescription: String,
            itemType: GQLType,
            itemPaginatedListProvider: (DataFetchingEnvironment, P, Int, Int) -> PaginatedList<T>,
            arguments: List<GraphQLArgument> = emptyList()
    ): GraphQLFieldDefinition =
            createBasePaginatedListField(
                    cache,
                    fieldName,
                    fieldDescription,
                    itemType,
                    arguments
            ).dataFetcher { environment ->
                val source: P = environment.getSource<P>()
                val offset = environment.getArgument<Int>(ARG_OFFSET) ?: 0
                val size = environment.getArgument<Int>(ARG_SIZE) ?: PageRequest.DEFAULT_PAGE_SIZE
                itemPaginatedListProvider(
                        environment,
                        source,
                        offset,
                        size
                )
            }.build()

    /**
     * Creates a paginated field from a counter and list provider.
     *
     * @param fieldName Name of the field
     * @param fieldDescription Description of the field
     * @param itemType Type of the items being paginated
     * @param itemListCounter Function to provide the _total_ number of items, regardless of pagination
     * @param itemListProvider Function to provide a list of items restricted by pagination
     * @param arguments Optional list of arguments to add to the field
     */
    fun <P, T> createPaginatedField(
            cache: GQLTypeCache,
            fieldName: String,
            fieldDescription: String,
            itemType: GQLType,
            itemListCounter: (DataFetchingEnvironment, P) -> Int,
            itemListProvider: (DataFetchingEnvironment, P, Int, Int) -> List<T>,
            arguments: List<GraphQLArgument> = emptyList()
    ): GraphQLFieldDefinition =
            createBasePaginatedListField(
                    cache, fieldName, fieldDescription, itemType, arguments
            ).dataFetcher { environment ->
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
            }.build()

    private fun createBasePaginatedListField(
            cache: GQLTypeCache,
            fieldName: String,
            fieldDescription: String,
            itemType: GQLType,
            arguments: List<GraphQLArgument>
    ): GraphQLFieldDefinition.Builder =
            GraphQLFieldDefinition.newFieldDefinition()
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
                    .type(createPaginatedList(cache, itemType))

    /**
     * Creates a paginated GraphQL list type, linked to an actual
     * [PaginatedList] of objects.
     *
     * @param itemType Type of item in the list
     */
    private fun createPaginatedList(
            cache: GQLTypeCache,
            itemType: GQLType
    ): GraphQLObjectType {
        val paginatedListTypeName = "${itemType.typeName}Paginated"
        return cache.getOrCreate(
                paginatedListTypeName,
                {
                    GraphQLObjectType.newObject()
                            .name(paginatedListTypeName)
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
        )
    }
}