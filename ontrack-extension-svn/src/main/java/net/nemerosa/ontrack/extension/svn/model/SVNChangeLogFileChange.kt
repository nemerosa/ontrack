package net.nemerosa.ontrack.extension.svn.model

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType

class SVNChangeLogFileChange(
        val revisionInfo: SVNRevisionInfo,
        val changeType: SCMChangeLogFileChangeType,
        val url: String
)
