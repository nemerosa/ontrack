package net.nemerosa.ontrack.extension.av

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.av.config.*
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.test.TestUtils.uid
import java.util.*

object AutoVersioningTestFixtures {

    fun Build.createOrder(
        targetBranch: Branch,
        targetVersion: String = "2.0.0",
        sourcePromotionRunId: Int? = null,
    ) = AutoVersioningOrder(
        uuid = UUID.randomUUID().toString(),
        sourceProject = project.name,
        sourceBuildId = id(),
        sourcePromotionRunId = sourcePromotionRunId,
        sourcePromotion = "GOLD",
        sourceBackValidation = null,
        branch = targetBranch,
        targetPath = "gradle.properties",
        targetRegex = null,
        targetProperty = "version",
        targetPropertyRegex = null,
        targetPropertyType = null,
        targetVersion = targetVersion,
        autoApproval = true,
        upgradeBranchPattern = "feature/version-<version>",
        postProcessing = null,
        postProcessingConfig = NullNode.instance,
        validationStamp = null,
        autoApprovalMode = AutoApprovalMode.SCM,
        reviewers = emptyList(),
        prTitleTemplate = null,
        prBodyTemplate = null,
        prBodyTemplateFormat = null,
        additionalPaths = emptyList(),
    )

    fun Branch.createOrder(
        sourceProject: String,
        targetVersion: String = "2.0.0",
        targetPaths: List<String> = listOf("gradle.properties"),
        sourceBuildId: Int? = null,
        sourcePromotionRunId: Int? = null,
        sourceBackValidation: String? = null,
        prTitleTemplate: String? = null,
        prBodyTemplate: String? = null,
        prBodyTemplateFormat: String? = null,
    ) = AutoVersioningOrder(
        uuid = UUID.randomUUID().toString(),
        sourceProject = sourceProject,
        sourceBuildId = sourceBuildId,
        sourcePromotionRunId = sourcePromotionRunId,
        sourcePromotion = "GOLD",
        sourceBackValidation = sourceBackValidation,
        branch = this,
        targetPath = AutoVersioningSourceConfigPath.toString(targetPaths),
        targetRegex = null,
        targetProperty = "version",
        targetPropertyRegex = null,
        targetPropertyType = null,
        targetVersion = targetVersion,
        autoApproval = true,
        upgradeBranchPattern = "feature/version-<version>",
        postProcessing = null,
        postProcessingConfig = NullNode.instance,
        validationStamp = null,
        autoApprovalMode = AutoApprovalMode.SCM,
        reviewers = emptyList(),
        prTitleTemplate = prTitleTemplate,
        prBodyTemplate = prBodyTemplate,
        prBodyTemplateFormat = prBodyTemplateFormat,
        additionalPaths = emptyList(),
    )

    fun sampleConfig() = AutoVersioningConfig(
        configurations = listOf(
            sourceConfig(),
            sourceConfig(),
        )
    )

    fun sourceConfig(
        sourceProject: String = uid("P"),
        sourceBranch: String = "master",
        sourcePromotion: String = "IRON",
        targetRegex: String? = null,
        targetPath: String = "gradle.properties",
        targetProperty: String? = "version",
        upgradeBranchPattern: String? = null,
        postProcessing: String? = null,
        postProcessingConfig: JsonNode? = null,
        autoApprovalMode: AutoApprovalMode? = null,
        notifications: List<AutoVersioningNotification>? = null,
    ) = AutoVersioningSourceConfig(
        sourceProject = sourceProject,
        sourceBranch = sourceBranch,
        sourcePromotion = sourcePromotion,
        targetPath = targetPath,
        targetProperty = targetProperty,
        targetRegex = targetRegex,
        targetPropertyRegex = null,
        targetPropertyType = null,
        autoApproval = null,
        upgradeBranchPattern = upgradeBranchPattern,
        postProcessing = postProcessing,
        postProcessingConfig = postProcessingConfig,
        validationStamp = null,
        autoApprovalMode = autoApprovalMode,
        notifications = notifications,
        qualifier = null,
        reviewers = null,
        prTitleTemplate = null,
        prBodyTemplate = null,
        prBodyTemplateFormat = null,
    )

}