package net.nemerosa.ontrack.extension.stash.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BitbucketProject(
        val id: Int,
        val key: String,
        val name: String
)
