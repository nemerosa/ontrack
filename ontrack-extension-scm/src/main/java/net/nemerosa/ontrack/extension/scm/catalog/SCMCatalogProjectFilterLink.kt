package net.nemerosa.ontrack.extension.scm.catalog

/**
 * Type of link between a SCM catalog entry and an Ontrack project
 */
enum class SCMCatalogProjectFilterLink {

    /**
     * Any entry or orphan project is accepted
     */
    ALL,

    /**
     * Only entries (not orphan projects) is accepted
     */
    ENTRY,

    /**
     * Only entries being linked to a project
     */
    LINKED,

    /**
     * Only entries having no corresponding project
     */
    UNLINKED,

    /**
     * Only orphan projects
     */
    ORPHAN

}