package net.nemerosa.ontrack.kdsl.connector

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseInto
import kotlin.reflect.KClass

inline fun <reified T : Any> ConnectorResponseBody.parse(): T = parseInto(T::class)

fun <T : Any> ConnectorResponseBody.parseInto(type: KClass<T>): T {
    val json = asJson()
    return try {
        json.parseInto(type)
    } catch (any: Exception) {
        println(json)
        throw any
    }
}

inline fun <reified T : Any> ConnectorResponseBody.parseOrNull(): T? = asJsonOrNull()?.parse()
