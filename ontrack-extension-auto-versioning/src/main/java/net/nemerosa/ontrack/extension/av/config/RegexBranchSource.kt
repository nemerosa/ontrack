package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.ordering.BranchOrderingService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * Default [BranchSource], using a regular expression to get the source branches.
 */
@Component
class RegexBranchSource(
    private val structureService: StructureService,
    private val branchDisplayNameService: BranchDisplayNameService,
    private val branchOrderingService: BranchOrderingService,
) : AbstractBranchSource("regex") {

    override fun getLatestBranch(
        config: String?,
        project: Project,
        targetBranch: Branch,
        promotion: String,
        includeDisabled: Boolean
    ): Branch? {
        val sourceRegex = config?.toRegex() ?: throw BranchSourceMissingConfigurationException(id)
        // Version-based ordering
        val versionComparator = branchOrderingService.getRegexVersionComparator(sourceRegex)
        // Gets the list of branches for the source project
        val allBranches = structureService.getBranchesForProject(project.id)
        val filteredBranches = if (includeDisabled) {
            allBranches
        } else {
            allBranches.filterNot { it.isDisabled }
        }
        return filteredBranches
            // ... filters them by regex, using their path
            .filter { sourceBranch ->
                // First on the technical build name, then on the SCM branch name (more costly)
                sourceRegex.matches(sourceBranch.name) || sourceRegex.matches(
                    branchDisplayNameService.getBranchDisplayName(
                        sourceBranch,
                        BranchNamePolicy.DISPLAY_NAME_OR_NAME,
                    )
                )
            }
            // ... order them by version
            .minWithOrNull(versionComparator)
    }
}