package net.nemerosa.ontrack.extension.scm.model

/**
 * Defines a list of filters
 */
class SCMFileChangeFilters(
    /**
     * List of filters
     */
    val filters: List<SCMFileChangeFilter>
) {

    fun save(filter: SCMFileChangeFilter): SCMFileChangeFilters {
        return withStore { store ->
            store[filter.name] = filter
        }
    }

    fun remove(name: String): SCMFileChangeFilters {
        return withStore { store ->
            store.remove(name)
        }
    }

    private fun withStore(action: (MutableMap<String, SCMFileChangeFilter>) -> Unit): SCMFileChangeFilters {
        val store = filters.associateBy { it.name }.toSortedMap()
        action(store)
        return SCMFileChangeFilters(store.values.toList())
    }

    companion object {
        fun create(): SCMFileChangeFilters {
            return SCMFileChangeFilters(emptyList())
        }
    }
}