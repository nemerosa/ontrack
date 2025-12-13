package net.nemerosa.ontrack.kdsl.spec.extension.stash

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.kdsl.spec.Configuration

@JsonIgnoreProperties(ignoreUnknown = true)
data class BitbucketServerConfiguration(
    override val name: String,
    val url: String,
    val user: String = "",
    val password: String = "",
) : Configuration