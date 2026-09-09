package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.model.structure.ID

/**
 * The checkpoint kinds the core knows about, and the ids built from them.
 *
 * Checkpoint ids share one namespace across every kind, so a promotion level id, a validation stamp
 * id and a slot UUID cannot simply be their raw identifiers: the type prefixes them. Ids built here
 * are deterministic, which is what lets #1707 keep a node's position across a refresh.
 *
 * The set of kinds is open - an extension may contribute one the core has never heard of - so these
 * constants are the ones the core itself contributes, not an exhaustive list.
 */
object DeliveryMapCheckpointTypes {

    /**
     * A promotion level of the branch, arrived at by being promoted.
     */
    const val PROMOTION_LEVEL = "promotion-level"

    /**
     * A validation stamp of the branch, arrived at by a run of any outcome.
     */
    const val VALIDATION_STAMP = "validation-stamp"

    /**
     * One checkpoint standing for every validation stamp matched by an auto promotion pattern, rather
     * than one checkpoint per stamp. It is labelled with the pattern, because that is what the
     * configuration actually says: everything matching this, not these forty named things.
     */
    const val VALIDATION_STAMP_PATTERN = "validation-stamp-pattern"

    /**
     * A name a configuration asked for and which matches nothing: a slot admission rule naming a
     * promotion level this branch does not have, or an environment this project has no slot in.
     *
     * It is a kind of its own rather than a flag on the kind it failed to find, because there is no
     * entity behind it: no id to link to, no build which could ever arrive at it. See
     * [DeliveryMapCheckpoint.unresolved].
     */
    const val UNRESOLVED = "unresolved"

    fun promotionLevel(id: ID): String = checkpointId(PROMOTION_LEVEL, id.value.toString())

    fun validationStamp(id: ID): String = checkpointId(VALIDATION_STAMP, id.value.toString())

    /**
     * The aggregate checkpoint of an auto promotion pattern is identified by the promotion level whose
     * configuration carries the pattern: there is at most one per promotion level.
     */
    fun validationStampPattern(promotionLevelId: ID): String =
        checkpointId(VALIDATION_STAMP_PATTERN, promotionLevelId.value.toString())

    /**
     * The id of an unresolved checkpoint is built from the [reference] kind it looked for and the
     * [name] it looked for, and from nothing else. Two rules asking for the same missing thing
     * therefore land on one checkpoint rather than one each, and the id survives a refresh, which is
     * what #1707 needs in order to keep the node where the user dragged it.
     *
     * The [reference] is part of the id because the two halves of the name space would otherwise
     * meet: a promotion level called `staging` and an environment called `staging` are not the same
     * missing thing.
     *
     * This is the one checkpoint id built out of a *name* rather than out of an id or a UUID, and it
     * is not escaped. It does not need to be: an entity name is `[A-Za-z0-9._-]+` and can hold no
     * separator, so two different names cannot produce one id.
     */
    fun unresolved(reference: String, name: String): String =
        checkpointId(UNRESOLVED, "$reference:$name")

    /**
     * Namespaces a raw identifier under a checkpoint [type].
     */
    fun checkpointId(type: String, id: String): String = "$type:$id"

}
