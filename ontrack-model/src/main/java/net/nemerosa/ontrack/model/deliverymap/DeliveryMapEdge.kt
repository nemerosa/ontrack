package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription

/**
 * A dependency between two checkpoints of a [DeliveryMap].
 *
 * Both ends are checkpoint *ids* rather than checkpoints, because an edge routinely joins two
 * checkpoints contributed by two different [DeliveryMapContributor]s - a slot admission rule naming a
 * promotion level, for instance.
 *
 * Edges always run from the prerequisite to the checkpoint that depends on it, whatever their [kind];
 * the kind says whether reaching the source *grants* the target or merely permits it.
 */
@APIDescription("A dependency between two checkpoints of a delivery map")
data class DeliveryMapEdge(
    @APIDescription("Identifier, stable across fetches")
    val id: String,
    @APIDescription("What the dependency means")
    val kind: DeliveryMapEdgeKind,
    @APIDescription("ID of the checkpoint the dependency runs from")
    val source: String,
    @APIDescription("ID of the checkpoint the dependency runs to")
    val target: String,
) {
    companion object {
        /**
         * Builds an edge with a deterministic id, so that a refresh sees the same edge rather than a
         * new one - the same reason checkpoint ids are deterministic.
         */
        fun of(kind: DeliveryMapEdgeKind, source: String, target: String) = DeliveryMapEdge(
            id = "${kind.name.lowercase()}:$source->$target",
            kind = kind,
            source = source,
            target = target,
        )
    }
}
