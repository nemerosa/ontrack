package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription

/**
 * What an edge of a [DeliveryMap] means.
 *
 * [UNLOCKS] and [REQUIRES] are kept apart because promotion prerequisites are expressed in ways with
 * opposite effects, and a map which drew them the same way would say that a configuration acts when
 * it only constrains. [EMITS] is neither: it runs the other way round, from a checkpoint to
 * something that checkpoint sets off, which depends on nothing and grants nothing.
 *
 * This set is CLOSED, unlike the set of checkpoint kinds: an extension contributes checkpoints
 * freely, and never a fourth meaning for a line. Adding [EMITS] was a deliberate reopening, argued
 * in ADR 0011 - the point of writing down why two words were enough is that the third is not free.
 */
enum class DeliveryMapEdgeKind {

    @APIDescription("Reaching the source grants the target by itself, as auto promotion does")
    UNLOCKS,

    @APIDescription("The target cannot be reached until the source has been. It constrains; it does not act")
    REQUIRES,

    @APIDescription("Reaching the source sets the target off, and nothing waits for the result")
    EMITS,

}
