package net.nemerosa.ontrack.extension.stash.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BitbucketServerCommit(
    val id: String,
    val displayId: String,
    val message: String,
    val authorTimestamp: Long?,
    val committerTimestamp: Long,
    val author: BitbucketServerAuthor?,
    val committer: BitbucketServerAuthor,
    val parents: List<BitbucketServerParentRef>?,
)
