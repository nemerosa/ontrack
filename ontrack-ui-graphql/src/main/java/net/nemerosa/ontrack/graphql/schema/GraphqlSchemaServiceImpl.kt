package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
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
        private val rootQueries: List<GQLRootQuery>,
        private val mutationProviders: List<MutationProvider>
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
        val mutationType = createMutationType(dictionary)
        return GraphQLSchema.newSchema()
                .additionalTypes(dictionary)
                .query(createQueryType())
                .mutation(mutationType)
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

    private fun createMutationType(dictionary: MutableSet<GraphQLType>): GraphQLObjectType {
        return newObject()
                .name(MUTATION)
                // Root mutations
                .fields(
                        mutationProviders.flatMap { provider ->
                            provider.mutations.map { mutation -> createMutation(mutation, dictionary) }
                        }
                )
                // OK
                .build()
    }

    private fun createMutation(mutation: Mutation, dictionary: MutableSet<GraphQLType>): GraphQLFieldDefinition {
        TODO("Not yet implemented")
    }

    companion object {
        const val QUERY = "Query"
        const val MUTATION = "Mutation"
    }

}
