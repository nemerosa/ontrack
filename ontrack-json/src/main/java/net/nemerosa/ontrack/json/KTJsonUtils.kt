package net.nemerosa.ontrack.json

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlin.reflect.KClass

private val mapper = ObjectMapperFactory.create()

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
fun <T> T.asJson(): JsonNode = if (this is JsonNode) {
    this
} else {
    JsonUtils.format(this)!!
}

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
inline fun <reified T : Any> JsonNode.parse(): T =
    parseInto(T::class)

/**
 * Parses any node into an object.
 */
fun <T : Any> JsonNode.parseInto(type: KClass<T>): T =
    try {
        mapper.treeToValue(this, type.java)
    } catch (ex: MismatchedInputException) {
        throw JsonParseException(
            userFriendlyMessage(ex)
        )
    } catch (ex: JsonProcessingException) {
        throw JsonParseException(ex)
    }

fun userFriendlyMessage(ex: MismatchedInputException): String {
    val pathDescription = ex.path
        .joinToString(separator = ".") { ref ->
            // A Reference can point to either a field or an array index
            // If ref.fieldName is null, we assume it's an array index
            ref.fieldName ?: "[${ref.index}]"
        }

    // ex.location might be null in some cases, so check for nullability
    val location = ex.location
    val line = location?.lineNr
    val column = location?.columnNr

    return buildString {
        append("There was a problem parsing the JSON")
        if (pathDescription.isNotBlank()) {
            append(" at path '$pathDescription'")
        }
        if (line != null && column != null && line >= 0 && column >= 0) {
            append(" (line $line, column $column)")
        }
    }
}

/**
 * Formatting a JSON node as a string
 */
fun JsonNode.format(): String = JsonUtils.toJSONString(this)

/**
 * Parses any node into an object or returns `null` if parsing fails
 */
inline fun <reified T> JsonNode.parseOrNull(): T? =
    try {
        if (this.isNull) {
            null
        } else {
            parse()
        }
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
@Deprecated(message = "Use getIntField", replaceWith = ReplaceWith("getIntField"))
fun JsonNode.getInt(field: String): Int? = getIntField(field)

/**
 * Gets a field as a JSON node, but returns `null` if this is a null node.
 */
fun JsonNode.getJsonField(field: String): JsonNode? =
    if (has(field)) {
        get(field)?.takeIf { !it.isNull }
    } else {
        null
    }

/**
 * Gets a required field as a JSON node, but returns `null` if this is a null node.
 */
fun JsonNode.getRequiredJsonField(field: String): JsonNode =
    getJsonField(field)
        ?: throw JsonMissingFieldException(field)

/**
 * Gets a field as [Int].
 */
fun JsonNode.getIntField(field: String): Int? =
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
fun JsonNode.getTextField(field: String): String? = if (has(field)) {
    get(field).takeIf { !it.isNull }?.asText()
} else {
    null
}

/**
 * Gets a list of strings
 */
fun JsonNode.getListStringField(field: String): List<String>? = if (has(field)) {
    get(field).map { it.asText() }
} else {
    null
}

/**
 * Gets a required string field
 */
fun JsonNode.getRequiredTextField(field: String): String =
    getTextField(field)
        ?: throw JsonParseException("Missing field $field")

/**
 * Gets a boolean field
 */
fun JsonNode.getBooleanField(field: String): Boolean? = if (has(field)) {
    get(field).takeIf { !it.isNull }?.asBoolean()
} else {
    null
}

/**
 * Gets a required boolean field
 */
fun JsonNode.getRequiredBooleanField(field: String): Boolean =
    getBooleanField(field)
        ?: throw JsonParseException("Missing field $field")

/**
 * Gets a required int field
 */
fun JsonNode.getRequiredIntField(field: String): Int =
    getIntField(field)
        ?: throw JsonParseException("Missing field $field")

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
