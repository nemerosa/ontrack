package net.nemerosa.ontrack.graphql.support

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.schema.MutationProvider
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asInputFields
import org.springframework.beans.factory.annotation.Autowired
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import kotlin.reflect.KClass

abstract class TypedMutationProvider protected constructor(
        private val validator: Validator
) : MutationProvider {

    @Autowired
    constructor() : this(Validation.buildDefaultValidatorFactory().validator)

    /**
     * Validates an input object
     */
    protected fun validateInput(input: Any) {
        val violations: Set<ConstraintViolation<*>> = validator.validate(input)
        if (violations.isNotEmpty()) {
            throw MutationInputValidationException(violations)
        }
    }

    /**
     * Builds a mutation which does not return anything but allows the declaration of arbitrary fields.
     *
     * Suitable for deletions.
     */
    fun <I : Any> unitMutation(
            name: String,
            description: String,
            input: KClass<I>,
            outputFields: List<GraphQLFieldDefinition> = emptyList(),
            fetcher: (I) -> Unit
    ): Mutation = UnitTypedMutation(name, description, input, outputFields, fetcher)

    /**
     * Re-ified version of [unitMutation].
     */
    inline fun <reified I : Any> unitMutation(
            name: String,
            description: String,
            outputFields: List<GraphQLFieldDefinition> = emptyList(),
            noinline fetcher: (I) -> Unit
    ) = unitMutation(
            name = name,
            description = description,
            input = I::class,
            outputFields = outputFields,
            fetcher = fetcher
    )

    /**
     * Defines a [mutation][Mutation] which accepts one [input] object and
     * returns exactly zero or one output.
     *
     * @param name Name of this mutation
     * @param description Description of this mutation
     * @param input Type of the input object (used as the type of the `input` field)
     * @param outputName Name of the output object (used as a field in the returned type)
     * @param outputDescription Description of the output field
     * @param outputType Type of the output (will be [mapped][toTypeRef] to an actual & existing GraphQL output type)
     * @param fetcher Code whichs runs the actual mutation and returns the output object
     */
    fun <I : Any, T : Any> simpleMutation(
            name: String,
            description: String,
            input: KClass<I>,
            outputName: String,
            outputDescription: String,
            outputType: KClass<T>,
            fetcher: (I) -> T
    ): Mutation = SimpleTypedMutation(name, description, input, outputName, outputDescription, outputType, fetcher)

    inner class SimpleTypedMutation<I : Any, T : Any>(
            override val name: String,
            override val description: String,
            private val input: KClass<I>,
            private val outputName: String,
            outputDescription: String,
            outputType: KClass<T>,
            private val fetcher: (I) -> T
    ) : Mutation {

        override val inputFields: List<GraphQLInputObjectField> = asInputFields(input)

        override val outputFields: List<GraphQLFieldDefinition> = listOf(
                objectField(outputType, outputName, outputDescription)
        )

        override fun fetch(env: DataFetchingEnvironment): Any {
            val input = mutationInput(input, env)
            validateInput(input)
            return mapOf(outputName to fetcher(input))
        }
    }

    inner class UnitTypedMutation<I : Any>(
            override val name: String,
            override val description: String,
            private val input: KClass<I>,
            override val outputFields: List<GraphQLFieldDefinition>,
            private val fetcher: (I) -> Unit
    ) : Mutation {

        override val inputFields: List<GraphQLInputObjectField> = asInputFields(input)

        override fun fetch(env: DataFetchingEnvironment): Any {
            val input = mutationInput(input, env)
            validateInput(input)
            fetcher(input)
            // Nothing to return
            return Unit
        }
    }

}