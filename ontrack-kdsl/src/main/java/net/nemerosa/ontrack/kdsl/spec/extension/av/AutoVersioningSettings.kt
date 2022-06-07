package net.nemerosa.ontrack.kdsl.spec.extension.av

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AutoVersioningSettings(
    val enabled: Boolean,
)
