package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import net.nemerosa.ontrack.common.CachedSupplier
import org.springframework.stereotype.Service

@Service
class GraphqlSchemaServiceImpl(
        private val types: List<GQLType>,
        private val inputTypes: List<GQLInputType<*>>,
        private val interfaces: List<GQLInterface>,
        private val rootQueries: List<GQLRootQuery>
) : GraphqlSchemaService {

    private val schemaSupplier = CachedSupplier.of { this.createSchema() }

    override val schema: GraphQLSchema
        get() = schemaSupplier.get()

    private fun createSchema(): GraphQLSchema {
        // All types
        val cache = GQLTypeCache()
        val dictionary = mutableSetOf<GraphQLType>()
        dictionary.addAll(types.map { it.createType(cache) })
        dictionary.addAll(interfaces.map { it.createInterface() })
        dictionary.addAll(inputTypes.map { it.createInputType() })
        return GraphQLSchema.newSchema()
                .query(createQueryType())
                .additionalTypes(dictionary)
                .build()
    }

    private fun createQueryType(): GraphQLObjectType {
        return newObject()
                .name(QUERY)
                // Root queries
                .fields(
                        rootQueries.map { it.fieldDefinition }
                )
                // OK
                .build()
    }

    companion object {
        const val QUERY = "Query"
    }

}
