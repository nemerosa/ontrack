package net.nemerosa.ontrack.graphql.schema

import graphql.schema.*
import graphql.schema.GraphQLObjectType.newObject
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
        // Create the mutation input type
        val inputType = createMutationInputType(mutation, dictionary)
        // Create the mutation output type
        val outputType = createMutationOutputType(mutation, dictionary)
        // Create the mutation field
        val field = GraphQLFieldDefinition.newFieldDefinition()
                .name(mutation.name)
                .description(mutation.description)
                .argument {
                    it.name("input")
                            .description("Input for the mutation")
                            .type(inputType)
                }
                .type(outputType)
                .build()
        // Data fetching
        GraphQLCodeRegistry.newCodeRegistry()
                .dataFetcher(outputType, field) { env -> mutationFetcher(mutation, env) }
                .build()
        // OK
        return field
    }

    private fun mutationFetcher(mutation: Mutation, env: DataFetchingEnvironment): Any {
        return try {
            mutation.fetch(env)
        } catch (ex: Exception) {
            TODO("Management of errors")
        }
    }

    private fun createMutationOutputType(mutation: Mutation, dictionary: MutableSet<GraphQLType>): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name("${mutation.typePrefix}Payload")
                    .description("Output type for the ${mutation.name} mutation.")
                    .fields(mutation.outputFields)
                    // TODO Error management fields
                    .build()
                    .apply { dictionary.add(this) }

    private fun createMutationInputType(mutation: Mutation, dictionary: MutableSet<GraphQLType>): GraphQLInputType =
            GraphQLInputObjectType.newInputObject()
                    .name("${mutation.typePrefix}Input")
                    .description("Input type for the ${mutation.name} mutation.")
                    .fields(mutation.inputFields)
                    .build()
                    .apply { dictionary.add(this) }

    private val Mutation.typePrefix: String get() = name.capitalize()

    companion object {
        const val QUERY = "Query"
        const val MUTATION = "Mutation"
    }

}
