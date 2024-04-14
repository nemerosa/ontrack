package net.nemerosa.ontrack.kdsl.spec.extension.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlackSettings(
    val enabled: Boolean,
    val token: String,
    val emoji: String? = "",
)
