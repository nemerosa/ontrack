package net.nemerosa.ontrack.extension.svn.model

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFile
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType

class SVNChangeLogFile(
        override val path: String,
        val url: String
) : SCMChangeLogFile {

    val changes = mutableListOf<SVNChangeLogFileChange>()

    override val changeTypes: List<SCMChangeLogFileChangeType>
        get() = changes.map { it.changeType }

    fun addChange(change: SVNChangeLogFileChange): SVNChangeLogFile {
        changes.add(change)
        return this
    }
}
