package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Default [BranchSource], using a regular expression to get the source branches.
 */
@Component
class RegexBranchSource(
    private val structureService: StructureService,
    private val branchDisplayNameService: BranchDisplayNameService,
) : AbstractBranchSource("regex") {

    private val ordering = OptionalVersionBranchOrdering(branchDisplayNameService)

    override fun getLatestBranch(config: String?, project: Project, targetBranch: Branch, promotion: String): Branch? {
        val sourceRegex = config?.toRegex() ?: throw BranchSourceMissingConfigurationException(id)
        // Version-based ordering
        val versionComparator = ordering.getComparator(config)
        // Gets the list of branches for the source project
        return structureService.getBranchesForProject(project.id)
            // ... filters them by regex, using their path
            .filter { sourceBranch ->
                // Path of the branch
                val sourcePath = branchDisplayNameService.getBranchDisplayName(sourceBranch)
                // Match check
                sourceRegex.matches(sourcePath) || sourceRegex.matches(sourceBranch.name)
            }
            // ... order them by version
            .minWithOrNull(versionComparator)
    }
}