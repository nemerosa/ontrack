package net.nemerosa.ontrack.graphql.support

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asInputFields
import javax.validation.ConstraintViolation
import javax.validation.Validation
import kotlin.reflect.KClass

fun <I : Any, T : Any> simpleMutation(
        name: String,
        description: String,
        input: KClass<I>,
        outputName: String,
        outputDescription: String,
        outputType: KClass<T>,
        fetcher: (I) -> T
): Mutation = SimpleTypedMutation(name, description, input, outputName, outputDescription, outputType, fetcher)

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
 * Re-ified version
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

private class SimpleTypedMutation<I : Any, T : Any>(
        override val name: String,
        override val description: String,
        private val input: KClass<I>,
        private val outputName: String,
        outputDescription: String,
        outputType: KClass<T>,
        private val fetcher: (I) -> T
) : ValidatingInputMutation<I>() {

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

private class UnitTypedMutation<I : Any>(
        override val name: String,
        override val description: String,
        private val input: KClass<I>,
        override val outputFields: List<GraphQLFieldDefinition>,
        private val fetcher: (I) -> Unit
) : ValidatingInputMutation<I>() {

    override val inputFields: List<GraphQLInputObjectField> = asInputFields(input)

    override fun fetch(env: DataFetchingEnvironment): Any {
        val input = mutationInput(input, env)
        validateInput(input)
        fetcher(input)
        // Nothing to return
        return Unit
    }
}

/**
 * [Mutation] which can validate an input
 */
private abstract class ValidatingInputMutation<I : Any> : Mutation {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    protected fun validateInput(input: I) {
        val violations: Set<ConstraintViolation<I>> = validator.validate(input)
        if (violations.isNotEmpty()) {
            throw MutationInputValidationException(violations)
        }
    }

}