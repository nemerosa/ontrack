package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkflowRun(
    val id: Long,
    @JsonProperty("head_branch")
    val headBranch: String,
    val status: String,
    val conclusion: String?,
) {
    /**
     * If the run is finished and successful, returns `true`.
     *
     * If the run is finished and not successful, returns `false`.
     *
     * If the run is not finished, returns `null`
     */
    val success: Boolean?
        get() = if (status == "completed") {
            conclusion == "success"
        } else {
            null
        }

}