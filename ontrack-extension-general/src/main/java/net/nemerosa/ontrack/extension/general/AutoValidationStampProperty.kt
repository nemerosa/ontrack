package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.annotation.JsonProperty

data class AutoValidationStampProperty @JvmOverloads constructor(
    @JsonProperty("autoCreate")
    val isAutoCreate: Boolean,
    @JsonProperty("autoCreateIfNotPredefined")
    val isAutoCreateIfNotPredefined: Boolean = false,
)
