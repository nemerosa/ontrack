package net.nemerosa.ontrack.common

/**
 * Forward synchronization from/to a given collection
 */
fun <A, B> syncForward(
    from: Collection<A>,
    to: Collection<B>,
    code: SyncForwardBuilder<A, B>.() -> Unit
) {
    val builder = SyncForwardBuilder(from, to)
    builder.code()
    val sync = builder.build()
    sync.runSync()
}

/**
 * Forward synchronization from a list of another, using a simple ID property check
 *
 * @param E Type of items
 */
fun <E> mergeList(
    target: List<E>,
    changes: List<E>,
    idFn: (E) -> Any,
    mergeFn: (e: E, existing: E) -> E,
): List<E> {
    val result = target.toMutableList()
    syncForward(
        from = changes,
        to = target,
    ) {
        equality { a, b -> idFn(a) == idFn(b) }
        onCreation { item -> result += item }
        onDeletion { }
        onModification { e, existing ->
            val index = result.indexOfFirst { idFn(it) == idFn(existing) }
            result[index] = mergeFn(e, existing)
        }
    }
    return result.toList()
}

/**
 * Forward synchronization from a map of another, using keys.
 *
 * @param E Type of items
 */
fun <K, E> mergeMap(
    target: Map<K, E>,
    changes: Map<K, E>,
    mergeFn: (e: E, existing: E) -> E,
): Map<K, E> {
    val result = target.toList().toMutableList()
    syncForward(
        from = changes.toList(),
        to = target.toList(),
    ) {
        equality { a, b -> a.first == b.first }
        onCreation { item -> result += item }
        onDeletion { }
        onModification { e, existing ->
            val index = result.indexOfFirst { it.first == existing.first }
            result[index] = existing.first to mergeFn(e.second, existing.second)
        }
    }
    return result.toMap()
}

/**
 * Forward synchronization builder
 */
class SyncForwardBuilder<A, B>(
    private val from: Collection<A>,
    private val to: Collection<B>
) {

    private var equality: (a: A, b: B) -> Boolean = { a, b -> a == b }
    private var onCreation: (a: A) -> Unit = {}
    private var onModification: (a: A, b: B) -> Unit = { _, _ -> }
    private var onDeletion: (b: B) -> Unit = { }

    fun equality(equality: (a: A, b: B) -> Boolean) {
        this.equality = equality
    }

    fun onCreation(creator: (a: A) -> Unit) {
        this.onCreation = creator
    }

    fun onModification(updater: (a: A, existing: B) -> Unit) {
        this.onModification = updater
    }

    fun onDeletion(deleter: (existing: B) -> Unit) {
        this.onDeletion = deleter
    }

    fun build() = SyncForward(
        source = from,
        target = to,
        equality = equality,
        onCreation = onCreation,
        onModification = onModification,
        onDeletion = onDeletion
    )

}