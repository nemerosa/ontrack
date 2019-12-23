package net.nemerosa.ontrack.extension.scm.catalog

interface SCMCatalog {

    /**
     * Collects all catalog entries
     */
    fun collectSCMCatalog(logger: (String) -> Unit)

}