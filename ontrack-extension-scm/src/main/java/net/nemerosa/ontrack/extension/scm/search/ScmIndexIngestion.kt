package net.nemerosa.ontrack.extension.scm.search

import java.time.LocalDateTime

data class ScmIndexIngestion(
    val lastIngestedAt: LocalDateTime,
    val lastCommit: String,
    val lastCommitTimestamp: LocalDateTime,
)
