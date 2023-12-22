package net.nemerosa.ontrack.model.structure

/**
 * Orders two entities by increasing ID
 */
fun <E : Entity> sortById(a: E, b: E): Pair<E, E> =
    if (a.id() < b.id()) {
        a to b
    } else {
        b to a
    }
