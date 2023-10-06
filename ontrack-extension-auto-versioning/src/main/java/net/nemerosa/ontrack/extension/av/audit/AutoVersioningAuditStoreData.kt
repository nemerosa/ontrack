package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class AutoVersioningAuditStoreData(
    val sourceProject: String,
    val sourceBuildId: Int?, // Nullable for backward compatibility
    val sourcePromotion: String?, // Nullable for backward compatibility
    val sourceBackValidation: String?, // Nullable for backward compatibility
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
    val reviewers: List<String>?, // Nullable for backward compatibility
) {
    val mostRecentState
        get() = states.first().state

    val running: Boolean get() = mostRecentState.isRunning

    fun addState(newState: AutoVersioningAuditEntryState) = AutoVersioningAuditStoreData(
        sourceProject = sourceProject,
        sourceBuildId = sourceBuildId,
        sourcePromotion = sourcePromotion,
        sourceBackValidation = sourceBackValidation,
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
        reviewers = reviewers,
    )
}