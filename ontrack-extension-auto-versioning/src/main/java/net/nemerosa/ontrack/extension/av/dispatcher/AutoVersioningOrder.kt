package net.nemerosa.ontrack.extension.av.dispatcher

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.extension.av.config.AutoVersioningTargetConfig
import net.nemerosa.ontrack.model.structure.Branch

data class AutoVersioningOrder(
    val uuid: String,
    val sourceProject: String,
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
) : AutoVersioningTargetConfig
