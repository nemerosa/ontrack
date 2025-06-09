package net.nemerosa.ontrack.extension.av.links

import net.nemerosa.ontrack.extension.api.BranchLinksDecorationExtension
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditEntry
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.links.BranchLinksDecoration
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.links.BranchLinksNode
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

/**
 * Gets the status of the last auto versioning process between two branches and displays it as a decoration
 * on the link in the branch dependency graph.
 */
@Component
class AutoVersioningBranchLinksDecorationExtension(
    extensionFeature: AutoVersioningExtensionFeature,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
) : AbstractExtension(extensionFeature), BranchLinksDecorationExtension {

    override val id: String = "auto_versioning"

    override fun getDecoration(
        source: BranchLinksNode,
        target: BranchLinksNode,
        direction: BranchLinksDirection,
    ): BranchLinksDecoration? {
        // Gets the correct dependency order
        val parent: Branch
        val dependency: Branch
        when (direction) {
            BranchLinksDirection.USING -> {
                parent = source.branch
                dependency = target.branch
            }
            BranchLinksDirection.USED_BY -> {
                parent = target.branch
                dependency = source.branch
            }
        }
        // Looks for the auto versioning configuration in the parent for the given source branch
        autoVersioningConfigurationService.getAutoVersioning(parent)
            ?.configurations
            ?.find {
                it.sourceProject == dependency.project.name &&
                        dependency.name.matches(it.sourceBranch.toRegex())
            }
            ?: return null// No auto versioning config on this link ==> no decoration
        // Gets the last audit between those two builds
        val entry = autoVersioningAuditQueryService.findByFilter(
            AutoVersioningAuditQueryFilter(
                project = parent.project.name,
                branch = parent.name,
                source = dependency.project.name,
                count = 1
            )
        ).firstOrNull()
        // Converts the last entry to a decoration
        return entry?.toDecoration(direction)
    }

    private fun AutoVersioningAuditEntry.toDecoration(direction: BranchLinksDirection) = BranchLinksDecoration(
        feature = feature.featureDescription,
        id = id,
        text = mostRecentState.state.name,
        description = decorationDescription(this),
        icon = decorationIcon(mostRecentState.state),
        url = null,
    )

    private fun decorationDescription(autoVersioningAuditEntry: AutoVersioningAuditEntry): String =
        "Auto version of <b>${autoVersioningAuditEntry.order.branch.project.name}/${autoVersioningAuditEntry.order.branch.name}</b> to <b>${autoVersioningAuditEntry.order.sourceProject}</b> version <b>${autoVersioningAuditEntry.order.targetVersion}</b>"

    private fun decorationIcon(state: AutoVersioningAuditState): String =
        when (state) {

            AutoVersioningAuditState.CREATED -> "created"
            AutoVersioningAuditState.RECEIVED -> "received"

            AutoVersioningAuditState.PROCESSING_START,
            AutoVersioningAuditState.PROCESSING_CREATING_BRANCH,
            AutoVersioningAuditState.POST_PROCESSING_END,
            AutoVersioningAuditState.PROCESSING_UPDATING_FILE,
            -> "processing"

            AutoVersioningAuditState.POST_PROCESSING_START -> "post_processing"

            AutoVersioningAuditState.PR_CREATING -> "pr_creating"

            AutoVersioningAuditState.PR_CREATED,
            AutoVersioningAuditState.PR_MERGED,
            AutoVersioningAuditState.PR_APPROVED,
            -> "processed"

            AutoVersioningAuditState.ERROR -> "error"

            AutoVersioningAuditState.PR_TIMEOUT -> "timeout"

            AutoVersioningAuditState.PROCESSING_ABORTED -> "aborted"
            AutoVersioningAuditState.PROCESSING_CANCELLED -> "cancelled"
        }

}