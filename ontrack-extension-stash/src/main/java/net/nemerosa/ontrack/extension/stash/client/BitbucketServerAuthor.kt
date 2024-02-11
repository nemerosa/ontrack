package net.nemerosa.ontrack.extension.stash.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BitbucketServerAuthor(
    val name: String,
    val emailAddress: String,
)
