package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SetAutoVersioningConfigMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoVersioningSourceConfigInput
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Branch

fun Branch.setAutoVersioningConfig(
    configurations: List<AutoVersioningSourceConfig>,
) {
    graphqlConnector.mutate(
        SetAutoVersioningConfigMutation(
            id.toInt(),
            configurations.map {
                AutoVersioningSourceConfigInput.builder()
                    .autoApproval(it.autoApproval)
                    .autoApprovalMode(
                        when (it.autoApprovalMode) {
                            AutoApprovalMode.CLIENT -> net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoApprovalMode.CLIENT
                            AutoApprovalMode.SCM -> net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoApprovalMode.SCM
                            null -> net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoApprovalMode.CLIENT
                        }
                    )
                    .postProcessing(it.postProcessing)
                    .postProcessingConfig(it.postProcessingConfig)
                    .sourceBranch(it.sourceBranch)
                    .sourceProject(it.sourceProject)
                    .sourcePromotion(it.sourcePromotion)
                    .targetPath(it.targetPath)
                    .targetProperty(it.targetProperty)
                    .targetPropertyRegex(it.targetPropertyRegex)
                    .targetPropertyType(it.targetPropertyType)
                    .targetRegex(it.targetRegex)
                    .upgradeBranchPattern(it.upgradeBranchPattern)
                    .validationStamp(it.validationStamp)
                    .build()
            }
        )
    ) { it?.setAutoVersioningConfig()?.fragments()?.payloadUserErrors()?.convert() }
}