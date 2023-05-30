package net.nemerosa.ontrack.graphql.schema

import graphql.schema.*
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.common.UserException
import net.nemerosa.ontrack.graphql.support.MutationInputValidationException
import org.springframework.stereotype.Service
import java.util.*

@Service
class GraphqlSchemaServiceImpl(
        private val types: List<GQLType>,
        private val inputTypes: List<GQLInputType<*>>,
        private val interfaces: List<GQLInterface>,
        private val enums: List<GQLEnum>,
        private val rootQueries: List<GQLRootQuery>,
        private val rootQueriesPlus: List<GQLRootQueries>,
        private val mutationProviders: List<MutationProvider>,
        private val contributors: List<GQLContributor>,
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
        dictionary.addAll(inputTypes.map { it.createInputType(dictionary) })
        dictionary.addAll(contributors.flatMap { it.contribute(cache, dictionary) })
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
                        rootQueries.map { it.getFieldDefinition() }
                )
                .fields(
                        rootQueriesPlus.flatMap { it.fieldDefinitions }
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
                .apply {
                    if (inputType != null) {
                        argument {
                            it.name("input")
                                    .description("Input for the mutation")
                                    .type(inputType)
                        }
                    }
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
                    // All mutation payloads inherit from the Payload type
                    .withInterface(GraphQLTypeReference(GQLInterfacePayload.PAYLOAD))
                    // Errors field
                    .field(GQLInterfacePayload.payloadErrorsField())
                    // OK
                    .build()
                    .apply { dictionary.add(this) }

    private fun createMutationInputType(mutation: Mutation, dictionary: MutableSet<GraphQLType>): GraphQLInputType? =
            mutation.inputFields(dictionary)
                    .takeIf { it.isNotEmpty() }
                    ?.let { fields ->
                        GraphQLInputObjectType.newInputObject()
                                .name("${mutation.typePrefix}Input")
                                .description("Input type for the ${mutation.name} mutation.")
                                .fields(fields)
                                .build()
                                .apply { dictionary.add(this) }
                    }

    private val Mutation.typePrefix: String get() = name.replaceFirstChar { it.titlecase() }

    companion object {
        const val QUERY = "Query"
        const val MUTATION = "Mutation"
    }

}
