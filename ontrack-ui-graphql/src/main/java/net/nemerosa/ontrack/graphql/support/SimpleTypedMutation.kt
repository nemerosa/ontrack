package net.nemerosa.ontrack.graphql.support

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asInputFields
import kotlin.reflect.KClass

class SimpleTypedMutation<I : Any, T : Any>(
        override val name: String,
        override val description: String,
        private val input: KClass<I>,
        outputName: String,
        outputDescription: String,
        outputType: KClass<T>,
        private val fetcher: (I) -> T
) : Mutation {

    override val inputFields: List<GraphQLInputObjectField> = asInputFields(input.java)

    override val outputFields: List<GraphQLFieldDefinition> = listOf(
            objectField(outputType, outputName, outputDescription)
    )

    override fun fetch(env: DataFetchingEnvironment): Any {
        val input = mutationInput(input, env)
        // TODO Validate
        return fetcher(input)
    }
}

fun <I : Any, T : Any> simpleMutation(
        name: String,
        description: String,
        input: KClass<I>,
        outputName: String,
        outputDescription: String,
        outputType: KClass<T>,
        fetcher: (I) -> T
) = SimpleTypedMutation(name, description, input, outputName, outputDescription, outputType, fetcher)
