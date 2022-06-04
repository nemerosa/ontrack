package net.nemerosa.ontrack.extension.av

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.test.TestUtils.uid

object AutoVersioningTestFixtures {

    fun sampleConfig() = AutoVersioningConfig(
        configurations = listOf(
            sourceConfig(),
            sourceConfig(),
        )
    )

    fun sourceConfig(
        sourceProject: String = uid("P"),
        targetRegex: String? = null,
        targetPath: String = "gradle.properties",
        targetProperty: String? = "version",
        upgradeBranchPattern: String? = null,
        postProcessing: String? = null,
        postProcessingConfig: JsonNode? = null,
    ) = AutoVersioningSourceConfig(
        sourceProject = sourceProject,
        sourceBranch = "master",
        sourcePromotion = "IRON",
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
        autoApprovalMode = AutoApprovalMode.SCM
    )

}