package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Describes the history of an auto-versioning decision process")
data class AutoVersioningTrail(
    @APIDescription("Trails per branch")
    val branches: List<AutoVersioningBranchTrail>,
) {

    fun init(configuredBranches: List<AutoVersioningConfiguredBranch>) =
        AutoVersioningTrail(
            branches = configuredBranches.map {
                AutoVersioningBranchTrail(
                    branch = it.branch,
                    configuration = it.configuration,
                )
            }
        )

    fun withOrder(
        branchTrail: AutoVersioningBranchTrail,
        order: AutoVersioningOrder,
    ) = withBranchTrail(
        branchTrail.order(order.uuid)
    )

    fun withBranchTrail(branchTrail: AutoVersioningBranchTrail) = AutoVersioningTrail(
        branches = branches.map {
            if (it.id == branchTrail.id) {
                branchTrail
            } else {
                it
            }
        }
    )

    companion object {
        fun empty() = AutoVersioningTrail(
            branches = emptyList(),
        )
    }

}
