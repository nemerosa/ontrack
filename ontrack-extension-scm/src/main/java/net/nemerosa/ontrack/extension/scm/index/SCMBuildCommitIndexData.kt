package net.nemerosa.ontrack.extension.scm.index

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime

data class SCMBuildCommitIndexData(
    val buildId: Int,
    val commitId: String,
    val commitTimestamp: LocalDateTime,
    /**
     * Mapped to SCMCommit.
     */
    val commitData: JsonNode,
)
