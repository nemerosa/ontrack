package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.common.times
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.BranchNamePolicy
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class SameReleaseBranchSource(
    private val branchDisplayNameService: BranchDisplayNameService,
    private val sameBranchSource: SameBranchSource,
    private val regexBranchSource: RegexBranchSource,
) : AbstractBranchSource("same-release") {

    override fun getLatestBranch(config: String?, project: Project, targetBranch: Branch, promotion: String, includeDisabled: Boolean): Branch? {
        val levels = (config?.takeIf { it.isNotBlank() } ?: "1").toInt()
        val pattern = "release\\/(${("\\d+" * levels).joinToString("\\.")})(\\..*)?"
        val path = branchDisplayNameService.getBranchDisplayName(targetBranch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
        val m = pattern.toRegex().matchEntire(path)
        return if (m != null) {
            val base = m.groupValues[1]
            val sourceRegex = "release\\/${base}\\..*"
            regexBranchSource.getLatestBranch(sourceRegex, project, targetBranch, promotion, includeDisabled)
        } else {
            // No match, returning the branch with the same name
            sameBranchSource.getLatestBranch(null, project, targetBranch, promotion, includeDisabled)
        }
    }

}