package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Branch

@APIDescription("Describes the history of an auto-versioning decision process")
data class AutoVersioningTrail(
    @APIDescription("List of branches configured for auto-versioning for the project and promotion")
    val potentialTargetBranches: List<Branch>,
    @APIDescription("List of branches being rejected and why")
    val rejectedTargetBranches: List<RejectedBranch>,
) {
    fun withPotentialTargetBranches(branches: List<Branch>) = AutoVersioningTrail(
        potentialTargetBranches = branches,
        rejectedTargetBranches = rejectedTargetBranches,
    )

    fun withRejectedBranch(branch: Branch, reason: String) = AutoVersioningTrail(
        potentialTargetBranches = potentialTargetBranches,
        rejectedTargetBranches = rejectedTargetBranches + RejectedBranch(
            branch = branch,
            reason = reason,
        )
    )

    companion object {
        fun init() = AutoVersioningTrail(
            potentialTargetBranches = emptyList(),
            rejectedTargetBranches = emptyList(),
        )
    }
}

@APIDescription("Reason why a branch is rejected for auto-versioning")
data class RejectedBranch(
    @APIDescription("Branch being rejected")
    val branch: Branch,
    @APIDescription("Rejection reason")
    val reason: String,
)

