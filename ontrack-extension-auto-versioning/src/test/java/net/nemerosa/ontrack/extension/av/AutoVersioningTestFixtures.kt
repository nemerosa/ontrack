package net.nemerosa.ontrack.extension.av

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningNotification
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.test.TestUtils.uid
import java.util.*

object AutoVersioningTestFixtures {

    fun Branch.createOrder(
        sourceProject: String,
        targetVersion: String = "2.0.0"
    ) = AutoVersioningOrder(
        uuid = UUID.randomUUID().toString(),
        sourceProject = sourceProject,
        branch = this,
        targetPaths = listOf("gradle.properties"),
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
        autoApprovalMode = AutoApprovalMode.SCM
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
    )

}