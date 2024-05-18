package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

data class AutoValidationStampProperty @JvmOverloads constructor(
    @JsonProperty("autoCreate")
    @APIName("autoCreate")
    @APIDescription("If true, creates validations from predefined ones")
    val isAutoCreate: Boolean,
    @JsonProperty("autoCreateIfNotPredefined")
    @APIName("autoCreateIfNotPredefined")
    @APIDescription("If true, creates validations even if not predefined")
    val isAutoCreateIfNotPredefined: Boolean = false,
)
