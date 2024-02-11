package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.GetBranchAutoVersioningConfigQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SetAutoVersioningConfigMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoVersioningNotificationInput
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
                    .backValidation(it.backValidation)
                    .versionSource(it.versionSource)
                    .buildLinkCreation(it.buildLinkCreation)
                    .reviewers(it.reviewers)
                    .notifications(
                        it.notifications?.map { n ->
                            AutoVersioningNotificationInput.builder()
                                .channel(n.channel)
                                .config(n.config)
                                .scope(n.scope.map { s ->
                                    net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoVersioningNotificationScope.valueOf(
                                        s.name
                                    )
                                })
                                .notificationTemplate(n.notificationTemplate)
                                .build()
                        }
                    )
                    .build()
            }
        )
    ) { it?.setAutoVersioningConfig()?.fragments()?.payloadUserErrors()?.convert() }
}

fun Branch.getAutoVersioningConfig(): List<AutoVersioningSourceConfig> =
    graphqlConnector.query(
        GetBranchAutoVersioningConfigQuery(id.toInt())
    )?.branches()?.firstOrNull()?.autoVersioningConfig()?.configurations()?.map {
        AutoVersioningSourceConfig(
            sourceProject = it.sourceProject(),
            sourceBranch = it.sourceBranch(),
            sourcePromotion = it.sourcePromotion(),
            targetPath = it.targetPath(),
            targetRegex = it.targetRegex(),
            targetProperty = it.targetProperty(),
            targetPropertyRegex = it.targetPropertyRegex(),
            targetPropertyType = it.targetPropertyType(),
            autoApproval = it.autoApproval(),
            upgradeBranchPattern = it.upgradeBranchPattern(),
            postProcessing = it.postProcessing(),
            postProcessingConfig = it.postProcessingConfig(),
            validationStamp = it.validationStamp(),
            autoApprovalMode = when (it.autoApprovalMode()) {
                net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoApprovalMode.CLIENT -> AutoApprovalMode.CLIENT
                net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoApprovalMode.SCM -> AutoApprovalMode.SCM
                else -> null
            },
            notifications = it.notifications()?.map { n ->
                AutoVersioningNotification(
                    channel = n.channel(),
                    config = n.config(),
                    scope = n.scope().map { s ->
                        AutoVersioningNotificationScope.valueOf(s.name)
                    },
                    notificationTemplate = n.notificationTemplate(),
                )
            },
        )
    } ?: emptyList()
