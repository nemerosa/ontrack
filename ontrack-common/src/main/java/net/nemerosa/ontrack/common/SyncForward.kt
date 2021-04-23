package net.nemerosa.ontrack.common

class SyncForward<A, B>(
        private val source: Collection<A>,
        private val target: Collection<B>,
        private val equality: (a: A, b: B) -> Boolean = { a, b -> a == b },
        private val onCreation: (a: A) -> Unit = {},
        private val onModification: (a: A, b: B) -> Unit = { _, _ -> },
        private val onDeletion: (b: B) -> Unit = { }
) {

    fun runSync() {
        // Decorated target collection
        val decoratedTarget = target.map {
            Target(it)
        }
        // For each item in the source collection (A)
        source.forEach { a ->
            // Checks if there is a corresponding target item (B)
            val b = decoratedTarget.find {
                equality(a, it.target)
            }
            // If not existing...
            if (b == null) {
                onCreation(a)
            }
            // Existing
            else {
                b.marked = true
                onModification(a, b.target)
            }
        }
        // Deletions
        decoratedTarget.filter { !it.marked }.forEach {
            onDeletion(it.target)
        }
    }

    private class Target<B>(
            val target: B
    ) {
        var marked: Boolean = false
    }

}