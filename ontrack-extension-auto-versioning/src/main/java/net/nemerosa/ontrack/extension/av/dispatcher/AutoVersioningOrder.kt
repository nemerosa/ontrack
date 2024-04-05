package net.nemerosa.ontrack.extension.av.dispatcher

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.extension.av.config.AutoVersioningTargetConfig
import net.nemerosa.ontrack.model.structure.Branch

data class AutoVersioningOrder(
    val uuid: String,
    // Source information
    val sourceProject: String,
    val sourceBuildId: Int?, // Nullable for backward compatibility
    val sourcePromotion: String?, // Nullable for backward compatibility
    val sourceBackValidation: String?, // Nullable for backward compatibility
    // Target information
    val branch: Branch,
    val targetPaths: List<String>,
    override val targetRegex: String?,
    override val targetProperty: String?,
    override val targetPropertyRegex: String?,
    override val targetPropertyType: String?,
    val targetVersion: String,
    val autoApproval: Boolean,
    val upgradeBranchPattern: String,
    val postProcessing: String?,
    val postProcessingConfig: JsonNode?,
    val validationStamp: String?,
    val autoApprovalMode: AutoApprovalMode,
    val reviewers: List<String>,
    val prTitleTemplate: String?,
    val prBodyTemplate: String?,
    val prBodyTemplateFormat: String?,
) : AutoVersioningTargetConfig {
    /**
     * Gets a meaningful commit message for this order
     */
    @JsonIgnore
    fun getCommitMessage() =
        "[auto-versioning] Upgrade of $sourceProject to version $targetVersion"
}
