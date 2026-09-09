package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription

/**
 * What an edge of a [DeliveryMap] means.
 *
 * The two are kept apart because promotion prerequisites are expressed in ways with opposite effects,
 * and a map which drew them the same way would say that a configuration acts when it only constrains.
 *
 * This set *is* closed, unlike the set of checkpoint kinds: an edge which neither grants nor permits
 * is not a dependency, and a third meaning would be a change to what the map says rather than a new
 * contributor.
 */
enum class DeliveryMapEdgeKind {

    @APIDescription("Reaching the source grants the target by itself, as auto promotion does")
    UNLOCKS,

    @APIDescription("The target cannot be reached until the source has been. It constrains; it does not act")
    REQUIRES,

}
