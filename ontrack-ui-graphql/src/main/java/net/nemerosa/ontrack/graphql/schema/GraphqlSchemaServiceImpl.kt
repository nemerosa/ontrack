package net.nemerosa.ontrack.graphql.schema

import graphql.schema.*
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.common.UserException
import net.nemerosa.ontrack.graphql.support.MutationInputValidationException
import org.springframework.stereotype.Service

@Service
class GraphqlSchemaServiceImpl(
        private val types: List<GQLType>,
        private val inputTypes: List<GQLInputType<*>>,
        private val interfaces: List<GQLInterface>,
        private val enums: List<GQLEnum>,
        private val rootQueries: List<GQLRootQuery>,
        private val mutationProviders: List<MutationProvider>
) : GraphqlSchemaService {

    override val schema: GraphQLSchema by lazy {
        createSchema()
    }

    private fun createSchema(): GraphQLSchema {
        // All types
        val cache = GQLTypeCache()
        val dictionary = mutableSetOf<GraphQLType>()
        dictionary.addAll(types.map { it.createType(cache) })
        dictionary.addAll(interfaces.map { it.createInterface() })
        dictionary.addAll(enums.map { it.createEnum() })
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
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(mutation.name)
                .description(mutation.description)
                .argument {
                    it.name("input")
                            .description("Input for the mutation")
                            .type(inputType)
                }
                .type(outputType)
                .dataFetcher { env -> mutationFetcher(mutation, env) }
                .build()
    }

    private fun mutationFetcher(mutation: Mutation, env: DataFetchingEnvironment): Any {
        return try {
            mutation.fetch(env)
        } catch (ex: Exception) {
            when (ex) {
                is MutationInputValidationException -> {
                    mapOf("errors" to ex.violations.map { cv ->
                        MutationInputValidationException.asUserError(cv)
                    })
                }
                is UserException -> {
                    val exception = ex::class.java.name
                    val error = UserError(
                        message = ex.message ?: exception,
                        exception = exception
                    )
                    mapOf("errors" to listOf(error))
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    private fun createMutationOutputType(mutation: Mutation, dictionary: MutableSet<GraphQLType>): GraphQLObjectType =
            newObject()
                    .name("${mutation.typePrefix}Payload")
                    .description("Output type for the ${mutation.name} mutation.")
                    .fields(mutation.outputFields)
                    // Error management fields
                    .field {
                        it.name("errors")
                                .description("List of errors")
                                .type(GraphQLList(GraphQLTypeReference(GQLTypeUserError.USER_ERROR)))
                    }
                    // OK
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
