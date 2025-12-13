package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.graphql.schema.GQLTypeBranchLinkExtension
import net.nemerosa.ontrack.model.structure.BranchLink
import org.springframework.stereotype.Component

/**
 * Extension which contributes the `autoVersioning` field to the `BranchLink` type.
 */
@Component
class AutoVersioningGQLTypeBranchLinkExtension(
    extensionFeature: AutoVersioningExtensionFeature,
    private val gqlTypeBuildAutoVersioning: GQLTypeBuildAutoVersioning,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
) : AbstractExtension(extensionFeature), GQLTypeBranchLinkExtension {

    override val additionalFields: List<GraphQLFieldDefinition> = listOf(
        GraphQLFieldDefinition.newFieldDefinition()
            .name("autoVersioning")
            .description("Auto versioning information for this link, in regards to the relationship from the source branch to the target branch.")
            .type(gqlTypeBuildAutoVersioning.typeRef)
            .dataFetcher { env ->
                val link: BranchLink = env.getSource()!!
                val source = link.sourceBuild.branch
                val target = link.targetBuild.branch

                // Gets the AV configuration between the source (parent) and the target (dependency)
                val config = autoVersioningConfigurationService.getAutoVersioningBetween(source, target)
                // Returns the contextual object for this field
                config?.run {
                    GQLTypeBuildAutoVersioning.Context(
                        source, target, this
                    )
                }
            }
            .build()
    )

}