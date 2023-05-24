package net.nemerosa.ontrack.kdsl.spec.extension.stash

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BitbucketServerConfiguration(
        val name: String,
        val url: String,
        val user: String = "",
        val password: String = "",
)