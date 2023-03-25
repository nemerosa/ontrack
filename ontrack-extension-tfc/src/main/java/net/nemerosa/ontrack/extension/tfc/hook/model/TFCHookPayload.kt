package net.nemerosa.ontrack.extension.tfc.hook.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TFCHookPayload(
    @JsonProperty("notification_configuration_id")
    val notificationConfigurationId: String,
    @JsonProperty("run_url")
    val runUrl: String?,
    @JsonProperty("run_id")
    val runId: String?,
    @JsonProperty("workspace_id")
    val workspaceId: String?,
    @JsonProperty("workspace_name")
    val workspaceName: String?,
    @JsonProperty("organization_name")
    val organizationName: String?,
    /**
     * List of notifications
     */
    val notifications: List<TFCHookPayloadNotification>,
)
