package net.nemerosa.ontrack.test

import java.util.*
import kotlin.test.fail

inline fun <reified T> assertIs(value: Any?, code: (T) -> Unit) {
    if (value is T) {
        code(value)
    } else {
        fail("Not a ${T::class.qualifiedName}")
    }
}

fun <T> assertPresent(o: Optional<T>, message: String = "Optional must be present", code: (T) -> Unit) {
    if (o.isPresent) {
        code(o.get())
    } else {
        fail(message)
    }
}
