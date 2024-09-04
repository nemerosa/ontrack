package net.nemerosa.ontrack.kdsl.spec.extension.av

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.GetBranchAutoVersioningConfigQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SetAutoVersioningConfigMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoVersioningNotificationInput
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoVersioningSourceConfigInput
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoVersioningSourceConfigPathInput
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Branch

fun Branch.setAutoVersioningConfig(
    configurations: List<AutoVersioningSourceConfig>,
) {
    graphqlConnector.mutate(
        SetAutoVersioningConfigMutation(
            id.toInt(),
            configurations.map { config ->
                AutoVersioningSourceConfigInput.builder()
                    .autoApproval(config.autoApproval)
                    .autoApprovalMode(
                        when (config.autoApprovalMode) {
                            AutoApprovalMode.CLIENT -> net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoApprovalMode.CLIENT
                            AutoApprovalMode.SCM -> net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoApprovalMode.SCM
                            null -> net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.AutoApprovalMode.CLIENT
                        }
                    )
                    .postProcessing(config.postProcessing)
                    .postProcessingConfig(config.postProcessingConfig)
                    .sourceBranch(config.sourceBranch)
                    .sourceProject(config.sourceProject)
                    .sourcePromotion(config.sourcePromotion)
                    .targetPath(config.targetPath)
                    .targetProperty(config.targetProperty)
                    .targetPropertyRegex(config.targetPropertyRegex)
                    .targetPropertyType(config.targetPropertyType)
                    .targetRegex(config.targetRegex)
                    .upgradeBranchPattern(config.upgradeBranchPattern)
                    .validationStamp(config.validationStamp)
                    .backValidation(config.backValidation)
                    .versionSource(config.versionSource)
                    .buildLinkCreation(config.buildLinkCreation)
                    .reviewers(config.reviewers)
                    .notifications(
                        config.notifications?.map { n ->
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
                    .prTitleTemplate(config.prTitleTemplate)
                    .prBodyTemplate(config.prBodyTemplate)
                    .prBodyTemplateFormat(config.prBodyTemplateFormat)
                    .additionalPathsInput(
                        Input.optional(
                            config.additionalPaths?.map { path ->
                                AutoVersioningSourceConfigPathInput.builder()
                                    .path(path.path)
                                    .regex(path.regex)
                                    .property(path.property)
                                    .propertyRegex(path.propertyRegex)
                                    .propertyType(path.propertyType)
                                    .versionSource(path.versionSource)
                                    .build()
                            }
                        )
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
        it.fragments().autoVersioningSourceConfigFragment().toAutoVersioningSourceConfig()
    } ?: emptyList()
