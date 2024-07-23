package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.model.structure.Branch

fun AutoVersioningTracking.withDisabledBranch(branchTrail: AutoVersioningBranchTrail) {
    withTrail {
        it.withBranchTrail(
            branchTrail.disabled()
        )
    }
}

fun AutoVersioningTracking.reject(branchTrail: AutoVersioningBranchTrail, reason: String) {
    withTrail {
        it.withBranchTrail(
            branchTrail.reject(reason)
        )
    }
}

fun AutoVersioningTracking.withNotLatestBranch(branchTrail: AutoVersioningBranchTrail, latestSourceBranch: Branch?) {
    if (latestSourceBranch != null) {
        reject(branchTrail, "Branch should be the latest branch at ${latestSourceBranch.entityDisplayName}")
    } else {
        reject(branchTrail, "There is no latest source branch existing")
    }
}