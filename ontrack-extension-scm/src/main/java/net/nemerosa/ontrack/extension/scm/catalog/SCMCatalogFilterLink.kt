package net.nemerosa.ontrack.extension.scm.catalog

/**
 * Type of link between a SCM catalog entry and an Ontrack project
 */
enum class SCMCatalogFilterLink {

    /**
     * Any entry is accepted
     */
    ALL,

    /**
     * Only entries being linked to a project
     */
    LINKED,

    /**
     * Only entries having no corresponding project
     */
    ORPHAN

}