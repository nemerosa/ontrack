package net.nemerosa.ontrack.extension.svn.model

import java.time.LocalDateTime

data class SVNReference(
        val path: String,
        val url: String,
        val revision: Long,
        val time: LocalDateTime
) {
    fun toLocation(): SVNLocation = SVNLocation(path, revision)
}
