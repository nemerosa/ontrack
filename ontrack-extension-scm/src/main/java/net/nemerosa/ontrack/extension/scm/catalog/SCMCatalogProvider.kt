package net.nemerosa.ontrack.extension.scm.catalog

interface SCMCatalogProvider {

    val id: String
    val entries: List<SCMCatalogSource>

}