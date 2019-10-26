package net.nemerosa.ontrack.extension.svn.model

import java.time.LocalDateTime

class SVNRevisionInfo(
        val revision: Long,
        val author: String,
        val dateTime: LocalDateTime,
        val path: String,
        val message: String,
        val revisionUrl: String
) {
    fun toLocation() = SVNLocation(path, revision)
}
