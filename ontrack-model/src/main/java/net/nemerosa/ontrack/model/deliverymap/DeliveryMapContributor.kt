package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.model.structure.Branch

/**
 * Contributes checkpoints and edges to the [DeliveryMap] of a branch.
 *
 * The seam has several contributors from the very start, and not because of the slots of #1704: both
 * of the properties which produce edges between promotion levels and validation stamps live in
 * `ontrack-extension-general` rather than in the core, so even the promotion and validation half of
 * the map is extension-owned.
 *
 * A contributor is injected as a plain Spring `List<DeliveryMapContributor>`, which is empty when no
 * extension provides one, so the map keeps working on an instance where an extension is absent or
 * unlicensed. [DeliveryMapService] isolates a failing contributor rather than losing the whole map.
 */
interface DeliveryMapContributor {

    /**
     * Contributes to the delivery map of the given [branch].
     *
     * A contributor must leave out what the current user is not allowed to see. It must *not* return
     * a placeholder for it: a permission denial and a broken configuration would then be
     * indistinguishable on screen, and users would learn to read an unresolved checkpoint (#1705) as
     * "probably just permissions".
     */
    fun contribute(branch: Branch): DeliveryMapContribution

}
