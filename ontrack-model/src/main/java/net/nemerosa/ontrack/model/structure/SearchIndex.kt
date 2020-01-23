package net.nemerosa.ontrack.model.structure

interface SearchIndex<T : SearchItem> {

    /**
     * Items to be indexed.
     *
     * Items can be returned as a sequence, allowing the ES indexation to be
     * processed in batches.
     */
    fun items(): Sequence<T>

}