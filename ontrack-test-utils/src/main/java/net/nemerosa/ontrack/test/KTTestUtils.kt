package net.nemerosa.ontrack.test

import com.fasterxml.jackson.databind.JsonNode
import java.util.*
import kotlin.test.assertTrue
import kotlin.test.fail

inline fun <reified T> assertIs(value: Any?, code: (T) -> Unit = {}) {
    if (value is T) {
        code(value)
    } else {
        fail("Not a ${T::class.qualifiedName}")
    }
}

fun <T> assertPresent(o: Optional<T>, message: String = "Optional must be present", code: (T) -> Unit = {}) {
    if (o.isPresent) {
        code(o.get())
    } else {
        fail(message)
    }
}

fun <T> assertNotPresent(o: Optional<T>, message: String = "Optional is not present") {
    if (o.isPresent) {
        fail(message)
    }
}

fun assertJsonNull(node: JsonNode?, message: String = "Node is expected to be null") {
    assertTrue(node == null || node.isNull, message)
}

fun assertJsonNotNull(
    node: JsonNode?,
    message: String = "Node is expected not to be null",
    code: JsonNode.() -> Unit = {}
) {
    if (node == null || node.isNull) {
        fail(message)
    } else {
        node.code()
    }
}
