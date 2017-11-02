package net.nemerosa.ontrack.test

import kotlin.test.fail

inline fun <reified T> assertIs(value: Any?, code: (T) -> Unit) {
    if (value is T) {
        code(value)
    } else {
        fail("Not a ${T::class.qualifiedName}")
    }
}
