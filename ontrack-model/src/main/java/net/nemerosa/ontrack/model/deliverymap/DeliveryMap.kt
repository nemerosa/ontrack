package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription

/**
 * What a build on a branch has to pass through on its way to an environment: the branch's promotion
 * levels and validation stamps, the project's slots, and the configured dependencies between them.
 *
 * It is configuration with progress painted onto it, not a history. See `CONTEXT.md`.
 */
@APIDescription("What a build on a branch has to pass through on its way to an environment")
data class DeliveryMap(
    @APIDescription("Nodes of the map")
    val checkpoints: List<DeliveryMapCheckpoint>,
    @APIDescription("Dependencies between the checkpoints")
    val edges: List<DeliveryMapEdge>,
) {
    companion object {
        val EMPTY = DeliveryMap(checkpoints = emptyList(), edges = emptyList())
    }
}
