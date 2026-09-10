package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.model.structure.Build

/**
 * What a build on a branch has to pass through on its way to an environment: the branch's promotion
 * levels and validation stamps, the project's slots, and the configured dependencies between them.
 *
 * It is configuration with progress painted onto it, not a history. See `CONTEXT.md`.
 *
 * @property head The branch's latest build, and what every [DeliveryMapArrival.lag] on the map is
 * counted against. It is deliberately NOT a checkpoint: edges here mean *unlocks* or *requires*, and
 * an edge from the branch's latest build to every checkpoint would mean neither - while fanning out
 * across the whole map at once. Each checkpoint states its own lag instead. Null on a branch with no
 * build at all, where there is nothing to be behind.
 */
@APIDescription("What a build on a branch has to pass through on its way to an environment")
data class DeliveryMap(
    @APIDescription("Nodes of the map")
    val checkpoints: List<DeliveryMapCheckpoint>,
    @APIDescription("Dependencies between the checkpoints")
    val edges: List<DeliveryMapEdge>,
    @APIDescription("The branch's latest build, which every checkpoint's lag is counted against")
    val head: Build? = null,
) {
    companion object {
        val EMPTY = DeliveryMap(checkpoints = emptyList(), edges = emptyList())
    }
}
