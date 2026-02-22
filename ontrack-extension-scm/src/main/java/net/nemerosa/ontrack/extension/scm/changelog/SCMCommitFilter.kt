package net.nemerosa.ontrack.extension.scm.changelog

import java.time.LocalDateTime

data class SCMCommitFilter(
    val sinceCommit: String?,
    val sinceCommitTimestamp: LocalDateTime?,
    val count: Int,
)
