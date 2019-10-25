package net.nemerosa.ontrack.extension.svn.model

import java.time.LocalDateTime

class SVNReference(
        val path: String,
        val url: String,
        val revision: Long,
        val time: LocalDateTime
) {
    fun toLocation() = SVNLocation(path, revision)
}
