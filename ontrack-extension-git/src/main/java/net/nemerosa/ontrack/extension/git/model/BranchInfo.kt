package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionRun

/**
 * Information associated to a branch (for a commit, an issue, etc.)
 *
 * @property branch Associated branch
 * @property firstBuild First build on this branch
 * @property promotions Information about the promotions on this branch
 */
class BranchInfo(
        val branch: Branch,
        val firstBuild: Build?,
        val promotions: List<PromotionRun>
)
