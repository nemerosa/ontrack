package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class MostRecentBranchSource(
    private val structureService: StructureService,
    private val branchDisplayNameService: BranchDisplayNameService,
) : AbstractBranchSource("most-recent") {

    private val ordering = OptionalVersionBranchOrdering(branchDisplayNameService)

    override fun getLatestBranch(config: String?, project: Project, targetBranch: Branch, promotion: String): Branch? {
        val sourceRegex = config?.toRegex() ?: throw BranchSourceMissingConfigurationException(id)
        // Version-based ordering
        val versionComparator = ordering.getComparator(config)
        // Gets the list of branches for the source project matching the regex
        val branches = structureService.getBranchesForProject(project.id)
            // ... filters them by regex, using their path
            .filter { sourceBranch ->
                // Path of the branch
                val sourcePath = branchDisplayNameService.getBranchDisplayName(sourceBranch)
                // Match check
                sourceRegex.matches(sourcePath) || sourceRegex.matches(sourceBranch.name)
            }
            // ... ordering them
            .sortedWith(versionComparator)
        // If only one branch or not at all
        if (branches.size <= 1) {
            return branches.firstOrNull()
        }
        // Now, finding the first branch with at least one build being promoted
        else {
            val sourceBranch = branches.firstOrNull { branch ->
                // Gets the promotion level for this branch
                val promotionLevel = structureService.findPromotionLevelByName(
                    branch.project.name, branch.name, promotion
                ).getOrNull()
                if (promotionLevel != null) {
                    structureService.getLastPromotionRunForPromotionLevel(promotionLevel) != null
                } else {
                    false // Not eligible
                }
            }
            // If not found, we return the latest branch
            return sourceBranch ?: branches.firstOrNull()
        }
    }
}