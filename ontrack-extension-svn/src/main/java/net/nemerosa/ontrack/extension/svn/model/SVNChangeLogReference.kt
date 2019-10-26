package net.nemerosa.ontrack.extension.svn.model

import com.fasterxml.jackson.annotation.JsonProperty

class SVNChangeLogReference(
        val path: String,
        val start: Long,
        val end: Long
) {
    @JsonProperty("none")
    val isNone: Boolean = (start == end)
}
