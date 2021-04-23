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