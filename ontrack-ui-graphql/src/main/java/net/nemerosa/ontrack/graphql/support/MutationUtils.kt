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
class MutationMissingInputFieldException(fieldName: String) :
    InputException("Cannot find any `input/$fieldName` in the mutation input.")

fun <T> getMutationInputField(env: DataFetchingEnvironment, fieldName: String): T? {
    val input: Map<String, String?> = env.getArgument("input") ?: throw MutationMissingInputException()
    @Suppress("UNCHECKED_CAST")
    return input[fieldName] as? T
}

fun <T> getRequiredMutationInputField(env: DataFetchingEnvironment, fieldName: String): T {
    return getMutationInputField<T>(env, fieldName) ?: throw MutationMissingInputFieldException(fieldName)
}
