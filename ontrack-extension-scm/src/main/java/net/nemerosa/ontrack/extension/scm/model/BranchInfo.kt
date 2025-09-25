package net.nemerosa.ontrack.extension.scm.model

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionRun

@APIDescription("Information associated to a branch (for a commit, an issue, etc.)")
class BranchInfo(
    @APIDescription("Associated branch")
    val branch: Branch,
    @APIDescription("First build on this branch")
    val firstBuild: Build?,
    @APIDescription("List of promotions on this branch")
    val promotions: List<PromotionRun>
) {
    @APIDescription("Checks if the branch info contains any information")
    val empty: Boolean = firstBuild == null && promotions.isEmpty()
}
