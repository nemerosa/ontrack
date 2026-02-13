package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig

data class StoredBranchTrail(
    val id: String,
    val project: String,
    val branch: String,
    val configuration: AutoVersioningSourceConfig,
    val rejectionReason: String?,
    val orderId: String?,
)