package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.model.structure.Branch

/**
 * Assembles the [DeliveryMap] of a branch from its [DeliveryMapContributor]s.
 */
interface DeliveryMapService {

    /**
     * Gets the delivery map of the given [branch].
     */
    fun getDeliveryMap(branch: Branch): DeliveryMap

}
