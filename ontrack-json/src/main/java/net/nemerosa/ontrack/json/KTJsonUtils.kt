package net.nemerosa.ontrack.json

import com.fasterxml.jackson.databind.JsonNode

/**
 * Map as JSON
 */
fun jsonOf(vararg pairs: Pair<*, *>) =
        mapOf(*pairs).toJson()!!

/**
 * Converts any object into JSON, or null if not defined.
 */
fun <T> T?.toJson(): JsonNode? =
        JsonUtils.format(this)

/**
 * Non-null JSON transformation
 */
fun <T> T.asJson(): JsonNode = JsonUtils.format(this)!!

/**
 * Parses any node into an object.
 */
inline fun <reified T> JsonNode.parse(): T =
        JsonUtils.parse(this, T::class.java)

/**
 * Formatting a JSON node as a string
 */
fun JsonNode.format() = JsonUtils.toJSONString(this)

/**
 * Gets a field as enum
 */
inline fun <reified E : Enum<E>> JsonNode.getEnum(field: String): E? {
    val text = path(field).asText()
    if (text.isNullOrBlank()) {
        return null
    } else {
        return enumValueOf<E>(text)
    }
}

/**
 * Gets a field as [Int].
 */
fun JsonNode.getInt(field: String): Int? =
        if (has(field)) {
            get(field).asInt()
        } else {
            null
        }

/**
 * Gets a required field as enum
 */
inline fun <reified E : Enum<E>> JsonNode.getRequiredEnum(field: String): E =
        getEnum<E>(field) ?: throw JsonMissingFieldException(field)
