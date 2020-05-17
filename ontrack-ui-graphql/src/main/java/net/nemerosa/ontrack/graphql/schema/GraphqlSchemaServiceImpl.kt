package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLSchema
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
        val dictionary =
                types.map { it.createType(cache) } +
                        interfaces.map { it.createInterface() } +
                        inputTypes.map { it.createInputType() }
        return GraphQLSchema.newSchema()
                .query(createQueryType())
                .additionalTypes(dictionary.toSet())
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
