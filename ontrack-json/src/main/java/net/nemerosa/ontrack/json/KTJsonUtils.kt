package net.nemerosa.ontrack.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
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
fun JsonNode.toJsonMap(): Map<String, *> = JsonUtils.toMap(asJson())

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

/**
 * Gets a string field
 */
fun JsonNode.getTextField(field: String) = path(field).asText()

/**
 * Merging two JSON nodes
 *
 * @receiver Left component
 * @param node Right component
 * @param priority What to do on identical leaf fields
 * @param arrays What to do on arrays
 * @param conflictResolution What to do when fields do not have the same type
 * @return Result of the merge
 */
fun JsonNode.merge(
    node: JsonNode,
    priority: JsonMergePriority = JsonMergePriority.RIGHT,
    arrays: JsonArrayMergePriority = JsonArrayMergePriority.APPEND,
    conflictResolution: JsonConflictResolution = JsonConflictResolution.ABORT,
): JsonNode =
    when {
        node::class == this::class -> {
            when (this) {
                is ObjectNode -> mergeObject(node as ObjectNode, priority, arrays, conflictResolution)
                is ArrayNode -> mergeArray(node as ArrayNode, arrays)
                else -> when (priority) {
                    JsonMergePriority.LEFT -> this
                    JsonMergePriority.RIGHT -> node
                }
            }
        }
        node.isNullOrNullNode() -> {
            this
        }
        this.isNullOrNullNode() -> {
            node
        }
        else -> {
            when (conflictResolution) {
                JsonConflictResolution.ABORT -> error("Cannot merge JSON because of type conflict.")
                JsonConflictResolution.LEFT -> this
                JsonConflictResolution.RIGHT -> node
            }
        }
    }

fun ArrayNode.mergeArray(
    node: ArrayNode,
    arrays: JsonArrayMergePriority,
): JsonNode = when (arrays) {
    JsonArrayMergePriority.LEFT -> this
    JsonArrayMergePriority.RIGHT -> node
    JsonArrayMergePriority.APPEND -> {
        val target = arrayNode()
        target.addAll(this)
        target.addAll(node)
        target
    }
}

fun ObjectNode.mergeObject(
    node: ObjectNode,
    priority: JsonMergePriority,
    arrays: JsonArrayMergePriority,
    conflictResolution: JsonConflictResolution,
): JsonNode {
    // All field names
    val names = mutableSetOf<String>()
    this.fieldNames().forEach { names += it }
    node.fieldNames().forEach { names += it }
    // Looping over all the fields
    val target = objectNode()
    names.forEach { name ->
        val value: JsonNode? = if (this.has(name) && node.has(name)) {
            this.get(name).merge(node.get(name), priority, arrays, conflictResolution)
        } else if (this.has(name)) {
            this.get(name)
        } else if (node.has(name)) {
            node.get(name)
        } else {
            null
        }
        if (value != null) {
            target.set<JsonNode>(name, value)
        }
    }
    // OK
    return target
}

/**
 * What to do in case of type difference
 */
enum class JsonConflictResolution {
    ABORT,
    LEFT,
    RIGHT
}

/**
 * Priorities for JSON merge at field level
 */
enum class JsonMergePriority {
    RIGHT,
    LEFT
}

/**
 * Priorities for JSON merge at array level
 */
enum class JsonArrayMergePriority {
    APPEND,
    RIGHT,
    LEFT
}
