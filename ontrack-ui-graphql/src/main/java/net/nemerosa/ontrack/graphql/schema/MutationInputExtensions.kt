package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import kotlin.reflect.KProperty

inline fun <reified T> MutationInput.getInputObject(name: String): T? =
    getInput<Any>(name)?.asJson()?.parse<T>()

/**
 * Gets an optional string from a property
 */
fun MutationInput.getString(property: KProperty<String?>): String? =
    getInput<String>(property.name)

/**
 * Gets a required int from a property
 */
fun MutationInput.getRequiredInt(property: KProperty<Int>): Int =
    getRequiredInput(property.name)

/**
 * Gets an optional int from a property
 */
fun MutationInput.getInt(property: KProperty<Int?>): Int? =
    getInput<Int>(property.name)

/**
 * Gets an optional string list from a property
 */
fun MutationInput.getStringList(property: KProperty<List<String>?>): List<String>? =
    getInputObject(property.name)
