package net.nemerosa.ontrack.model.deliverymap

/**
 * What one [DeliveryMapContributor] adds to the delivery map of a branch.
 *
 * A contributor contributes the checkpoints it *owns*, not every checkpoint its edges touch: an edge
 * may name a checkpoint contributed by someone else, and [DeliveryMapService] drops any edge left
 * with a missing end.
 */
data class DeliveryMapContribution(
    val checkpoints: List<DeliveryMapCheckpoint> = emptyList(),
    val edges: List<DeliveryMapEdge> = emptyList(),
) {
    companion object {
        val EMPTY = DeliveryMapContribution()
    }
}
