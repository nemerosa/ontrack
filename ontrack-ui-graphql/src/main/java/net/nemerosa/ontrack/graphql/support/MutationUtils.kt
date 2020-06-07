package net.nemerosa.ontrack.graphql.support

import graphql.schema.DataFetchingEnvironment
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.model.exceptions.InputException
import kotlin.reflect.KClass

inline fun <reified T : Any> mutationInput(env: DataFetchingEnvironment): T =
        mutationInput(T::class, env)

fun <T : Any> mutationInput(type: KClass<T>, env: DataFetchingEnvironment): T {
    val input = env.arguments["input"] ?: throw MutationMissingInputException()
    return input.asJson().parseInto(type)
}

class MutationMissingInputException : InputException("Cannot find any `input` in the mutation input.")
