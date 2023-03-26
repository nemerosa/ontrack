package net.nemerosa.ontrack.extension.tfc.hook

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TFCHookPayloadNotification(
    val message: String,
    val trigger: String,
    @JsonProperty("run_status")
    val runStatus: String?,
)
