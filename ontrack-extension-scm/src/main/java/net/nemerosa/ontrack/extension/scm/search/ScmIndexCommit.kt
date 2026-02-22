package net.nemerosa.ontrack.extension.scm.search

import java.time.LocalDateTime

data class ScmIndexCommit(
    val commitId: String,
    val commitShort: String,
    val commitTimestamp: LocalDateTime,
    val message: String,
)
