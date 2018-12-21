package net.nemerosa.ontrack.extension.git.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.git.model.GitCommit

class IndexableGitCommit(
        val commit: GitCommit
) {
    /**
     * Derived property for the timestamp
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val timestamp: Long = Time.toEpochMillis(commit.commitTime)
}
