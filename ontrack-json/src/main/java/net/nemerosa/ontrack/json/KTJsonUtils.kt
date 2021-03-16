package net.nemerosa.ontrack.json

import com.fasterxml.jackson.databind.JsonNode
import kotlin.reflect.KClass

/**
 * Parses a string as JSON
 */
fun String.parseAsJson(): JsonNode = JsonUtils.parseAsNode(this)

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
 * To a Map through JSON
 */
fun JsonNode.toJsonMap(): Map<String,*> = JsonUtils.toMap(asJson())

/**
 * Format as a string
 */
fun JsonNode.asJsonString(): String = JsonUtils.toJSONString(this)

/**
 * Parses any node into an object.
 */
inline fun <reified T> JsonNode.parse(): T =
        JsonUtils.parse(this, T::class.java)

/**
 * Parses any node into an object.
 */
inline fun <T : Any> JsonNode.parseInto(type: KClass<T>): T =
        JsonUtils.parse(this, type.java)

/**
 * Formatting a JSON node as a string
 */
fun JsonNode.format() = JsonUtils.toJSONString(this)

/**
 * Parses any node into an object or returns `null` if parsing fails
 */
inline fun <reified T> JsonNode.parseOrNull(): T? =
        try {
            parse()
        } catch (_: JsonParseException) {
            null
        }

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

/**
 * Checks if a JSON node can be considered as null, either by being `null` itself
 * or by being an instance of the [null node][JsonNode.isNull].
 */
fun JsonNode?.isNullOrNullNode() = this == null || this.isNull
