package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Branch
import java.util.*

@APIDescription("AV trail for a specific branch")
data class AutoVersioningBranchTrail(
    @APIDescription("Unique ID for this trail")
    val id: String = UUID.randomUUID().toString(),
    @APIDescription("Targeted branch")
    val branch: Branch,
    @APIDescription("Associated configuration")
    val configuration: AutoVersioningSourceConfig,
    @APIDescription("Rejection reason")
    val rejectionReason: String? = null,
    @APIDescription("AV order ID when actually scheduled")
    val orderId: String? = null,
) {

    fun order(orderId: String) = AutoVersioningBranchTrail(
        id = id,
        branch = branch,
        configuration = configuration,
        rejectionReason = rejectionReason,
        orderId = orderId,
    )

    fun disabled() = reject("Branch is disabled")

    fun reject(rejectionReason: String) = AutoVersioningBranchTrail(
        id = id,
        branch = branch,
        configuration = configuration,
        rejectionReason = rejectionReason,
        orderId = orderId,
    )

}
