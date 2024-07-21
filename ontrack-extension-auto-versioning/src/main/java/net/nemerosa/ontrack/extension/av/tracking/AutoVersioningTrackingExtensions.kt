package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.model.structure.Branch

fun AutoVersioningTracking.withRejectedBranch(branch: Branch, reason: String) {
    withTrail {
        it.withRejectedBranch(branch, reason)
    }
}

fun AutoVersioningTracking.withDisabledBranch(branch: Branch) {
    withRejectedBranch(branch, "Branch is disabled")
}

fun AutoVersioningTracking.withNotLatestBranch(eligibleTargetBranch: Branch, latestSourceBranch: Branch?) {
    if (latestSourceBranch != null) {
        withRejectedBranch(
            eligibleTargetBranch,
            "Branch should be the latest branch at ${latestSourceBranch.entityDisplayName}"
        )
    } else {
        withRejectedBranch(eligibleTargetBranch, "There is no latest source branch existing")
    }
}