package net.nemerosa.ontrack.extension.svn.model

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommit
import java.time.LocalDateTime

class SVNChangeLogRevision(
        val path: String,
        val level: Int,
        val revision: Long,
        override val author: String,
        override val timestamp: LocalDateTime,
        override val message: String,
        override val link: String,
        override val formattedMessage: String
) : SCMChangeLogCommit {

    override val id: String = revision.toString()

    override val shortId: String = id

    override val authorEmail: String? = null
}
