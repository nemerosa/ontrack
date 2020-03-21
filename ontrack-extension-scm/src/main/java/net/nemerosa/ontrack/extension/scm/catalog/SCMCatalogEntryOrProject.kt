package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.structure.Project

class SCMCatalogEntryOrProject private constructor(
        val project: Project?,
        val entry: SCMCatalogEntry?
) : Comparable<SCMCatalogEntryOrProject> {

    companion object {
        fun orphanProject(project: Project) = SCMCatalogEntryOrProject(project, null)
        fun entry(entry: SCMCatalogEntry, project: Project?) = SCMCatalogEntryOrProject(project, entry)
    }

    override fun compareTo(other: SCMCatalogEntryOrProject): Int =
            compareValuesBy(this, other,
                    { it.entry?.scm },
                    { it.entry?.config },
                    { it.entry?.repository },
                    { it.project?.name }
            )

}