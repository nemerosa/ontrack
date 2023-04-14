package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.extension.recordings.Recording
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class AutoVersioningAuditStoreData(
        val uuid: String,
        val sourceProject: String,
        val targetPaths: List<String>,
        val targetRegex: String?,
        val targetProperty: String?,
        val targetPropertyRegex: String?,
        val targetPropertyType: String?,
        val targetVersion: String,
        val autoApproval: Boolean,
        val upgradeBranchPattern: String,
        val postProcessing: String?,
        val postProcessingConfig: JsonNode?,
        val validationStamp: String?,
        val autoApprovalMode: AutoApprovalMode,
        val states: List<AutoVersioningAuditEntryState>,
        val routing: String,
        val queue: String?,
) : Recording {

    override val id: String = uuid

    override val startTime: LocalDateTime
        get() = TODO("Not yet implemented")

    override val endTime: LocalDateTime?
        get() = TODO("Not yet implemented")

    val mostRecentState
        get() = states.first().state

    val running: Boolean get() = mostRecentState.isRunning

    fun addState(newState: AutoVersioningAuditEntryState) = AutoVersioningAuditStoreData(
            uuid = uuid,
            sourceProject = sourceProject,
            targetPaths = targetPaths,
            targetRegex = targetRegex,
            targetProperty = targetProperty,
            targetPropertyRegex = targetPropertyRegex,
            targetPropertyType = targetPropertyType,
            targetVersion = targetVersion,
            autoApproval = autoApproval,
            upgradeBranchPattern = upgradeBranchPattern,
            postProcessing = postProcessing,
            postProcessingConfig = postProcessingConfig,
            validationStamp = validationStamp,
            autoApprovalMode = autoApprovalMode,
            // Adding the new state at the beginning
            states = listOf(newState) + states,
            routing = routing,
            queue = queue,
    )
}