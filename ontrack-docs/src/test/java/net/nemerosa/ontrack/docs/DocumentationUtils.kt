package net.nemerosa.ontrack.docs

import kotlin.reflect.KClass

fun isScalarClass(type: KClass<*>): Boolean = type in SCALAR_CLASSES

private val SCALAR_CLASSES: Set<KClass<*>> = setOf(
    Boolean::class,
    Byte::class,
    Short::class,
    Int::class,
    Long::class,
    Float::class,
    Double::class,
    Char::class,
    String::class,
)
