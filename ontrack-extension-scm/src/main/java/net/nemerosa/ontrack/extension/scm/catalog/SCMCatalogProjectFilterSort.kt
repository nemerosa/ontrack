package net.nemerosa.ontrack.extension.scm.catalog

enum class SCMCatalogProjectFilterSort(
        val sortingSelector: (SCMCatalogEntryOrProject) -> Comparable<*>?
) {

    REPOSITORY({ it.entry?.repository }),

    LAST_ACTIVITY({ it.entry?.lastActivity })

}