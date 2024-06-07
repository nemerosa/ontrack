package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.ordering.BranchOrderingService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class AutoDisablingBranchPatternsStaleBranchCheck(
    extensionFeature: StaleExtensionFeature,
    private val propertyService: PropertyService,
    private val branchOrderingService: BranchOrderingService,
) : AbstractExtension(extensionFeature), StaleBranchCheck {

    override fun isProjectEligible(project: Project): Boolean =
        propertyService.hasProperty(project, AutoDisablingBranchPatternsPropertyType::class.java)

    /**
     * If the branch is already disabled, no need to check it.
     */
    override fun isBranchEligible(branch: Branch): Boolean = !branch.isDisabled

    override fun getBranchStaleness(
        context: StaleBranchCheckContext,
        branch: Branch,
        lastBuild: Build?
    ): StaleBranchStatus? {
        // Gets the property
        val property =
            propertyService.getPropertyValue(branch.project, AutoDisablingBranchPatternsPropertyType::class.java)
                ?: return null
        // Getting the first matching pattern
        val item = property.items.firstOrNull {
            it.matches(branch.name)
        } ?: return null
        // Behaviour
        return when (item.mode) {
            AutoDisablingBranchPatternsMode.KEEP -> StaleBranchStatus.KEEP
            AutoDisablingBranchPatternsMode.DISABLE -> StaleBranchStatus.DISABLE
            AutoDisablingBranchPatternsMode.KEEP_LAST -> isLastBranch(context, branch, item)
        }
    }

    private fun isLastBranch(
        context: StaleBranchCheckContext,
        branch: Branch,
        item: AutoDisablingBranchPatternsPropertyItem
    ): StaleBranchStatus {
        // Sorted branches
        // The context key must depend on the item regex
        val contextKey = "sortedBranches-${item.hashCode()}"
        val sortedBranches = context.getContext(contextKey) {
            val allBranches: List<Branch> = context.getContext(StaleBranchCheckContext.ALL_BRANCHES) ?: emptyList()
            val filteredBranches = allBranches.filter {
                item.matches(it.name)
            }
            val ordering = branchOrderingService.getSemVerBranchOrdering(
                branchNamePolicy = BranchNamePolicy.NAME_ONLY,
            )
            filteredBranches.sortedWith(ordering)
        }
        // Is a last branch if part of the last N sorted branches
        val lastBranch = branch.id() in sortedBranches.take(item.keepLast).map { it.id() }
        return if (lastBranch) {
            StaleBranchStatus.KEEP
        } else {
            StaleBranchStatus.DISABLE
        }
    }
}