package net.nemerosa.ontrack.test

import java.util.*
import kotlin.test.fail

fun <T> assertPresent(o: Optional<T>, message: String = "Optional is not present", code: (T) -> Unit) {
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
