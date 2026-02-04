package net.nemerosa.ontrack.extension.stash.scm

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequestStatus

data class BitbucketServerPR(
    val id: Int,
    val state: String,
    val open: Boolean,
    val title: String,
) {
    @JsonIgnore
    val status: SCMPullRequestStatus = when (state) {
        "MERGED" -> SCMPullRequestStatus.MERGED
        "DECLINED" -> SCMPullRequestStatus.DECLINED
        "OPEN" -> SCMPullRequestStatus.OPEN
        else -> SCMPullRequestStatus.UNKNOWN
    }
}