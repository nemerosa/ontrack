package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

data class AutoVersioningQueuePayload(
    val order: AutoVersioningOrder,
) {
    fun routingIdentifier(): String =
        RoutingIdentifier(
            sourceProject = order.sourceProject,
            sourcePromotion = order.sourcePromotion ?: "",
            targetProject = order.branch.project.name,
            targetBranch = order.branch.name,
            paths = order.allPaths.map { it.path },
        ).toString()

    data class RoutingIdentifier(
        val sourceProject: String,
        val sourcePromotion: String,
        val targetProject: String,
        val targetBranch: String,
        val paths: List<String>,
    )
}
