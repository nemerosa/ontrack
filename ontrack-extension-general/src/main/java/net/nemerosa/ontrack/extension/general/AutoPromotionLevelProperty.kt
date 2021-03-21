package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.annotation.JsonProperty

data class AutoPromotionLevelProperty(
    @JsonProperty("autoCreate")
    val isAutoCreate: Boolean
)