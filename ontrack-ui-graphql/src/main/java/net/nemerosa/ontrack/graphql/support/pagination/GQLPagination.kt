package net.nemerosa.ontrack.graphql.support.pagination

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import org.springframework.stereotype.Component

@Component
class GQLTypePageRequest : GQLType {

    companion object {
        const val PAGE_REQUEST = "PageRequest"
    }

    override fun getTypeName() = PAGE_REQUEST

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(PAGE_REQUEST)
                .field {
                    it.name("offset")
                            .description("Offset for the page")
                            .type(GraphQLInt)
                }
                .field {
                    it.name("size")
                            .description("Size for the page")
                            .type(GraphQLInt)
                }
                .build()
    }
}

@Component
class GQLTypePageInfo(
        private val pageRequest: GQLTypePageRequest
): GQLType {

    companion object {
        const val PAGE_INFO = "PageInfo"
    }

    override fun getTypeName() = PAGE_INFO

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(PAGE_INFO)
                .field {
                    it.name("totalSize")
                            .description("Total known size of the list")
                            .type(GraphQLInt)
                }
                .field {
                    it.name("currentOffset")
                            .description("Offset for the current page")
                            .type(GraphQLInt)
                }
                .field {
                    it.name("currentSize")
                            .description("Size for the current page")
                            .type(GraphQLInt)
                }
                .field {
                    it.name("previousPage")
                            .description("Previous page offset and size")
                            .type(pageRequest.typeRef)
                }
                .field {
                    it.name("nextPage")
                            .description("Next page offset and size")
                            .type(pageRequest.typeRef)
                }
                .field {
                    it.name("pageTotal")
                            .description("Total number of pages")
                            .type(GraphQLInt)
                }
                .field {
                    it.name("pageIndex")
                            .description("Index of the page in the total number of pages (starting from 0)")
                            .type(GraphQLInt)
                }
                .build()
    }

}