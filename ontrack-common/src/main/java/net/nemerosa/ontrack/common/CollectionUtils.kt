package net.nemerosa.ontrack.common

/**
 * Moving an element into the place occupied by another element.
 */
fun <T> moveItem(items: List<T>, activeIndex: Int, overIndex: Int): List<T> {

    // If either index isn't found, or they are the same, return the list unchanged
    if (activeIndex < 0 || overIndex < 0 ||
        activeIndex >= items.size || overIndex >= items.size ||
        activeIndex == overIndex
    ) {
        return items
    }

    // Make a mutable copy so we don't mutate the original list
    val mutableList = items.toMutableList()

    // If the active index is before the over index
    //        o------->
    // * * * (A) * * (O) * *
    // ... or the active index is after the over index
    //        <-------o
    // * * * (0) * * (A) * *

    val movedItem = mutableList.removeAt(activeIndex)
    mutableList.add(overIndex, movedItem)

    // Return a new List (immutable)
    return mutableList.toList()
}

/**
 * Moving an element, identified by a key, into the place occupied by another element.
 */
fun <T, K> moveItem(items: List<T>, activeKey: K, overKey: K, key: (item: T) -> K): List<T> {
    val activeIndex = items.indexOfFirst { key(it) == activeKey }
    val overIndex = items.indexOfFirst { key(it) == overKey }
    return moveItem(items, activeIndex, overIndex)
}

