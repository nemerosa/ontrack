package net.nemerosa.ontrack.kdsl.spec.extension.tfc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TFCSettings(
    val enabled: Boolean,
    val token: String,
)
