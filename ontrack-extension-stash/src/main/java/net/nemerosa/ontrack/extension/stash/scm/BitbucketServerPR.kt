package net.nemerosa.ontrack.extension.stash.scm

data class BitbucketServerPR(
    val id: Int,
    val state: String,
    val open: Boolean,
    val title: String,
)