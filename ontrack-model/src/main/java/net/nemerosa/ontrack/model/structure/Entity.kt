package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.structure.ID.Companion.isDefined

/**
 * An `Entity` is a model object that has an [ID]. The state of this ID will determined the status
 * of this entity:
 *
 *  * *new* - the ID is not [set][ID.isSet].
 *  * *defined* - the ID is [set][ID.isSet].
 *
 */
interface Entity {

    /**
     * ID of the entity
     */
    val id: ID

    /**
     * Numeric value of the ID
     *
     * @throws IllegalStateException If the entity is not defined.
     */
    fun id(): Int {
        check(isDefined(id)) {
            "ID of the entity must be defined"
        }
        return id.value
    }

    companion object {

        @JvmStatic
        fun isEntityNew(e: Entity?, message: String) {
            check(e != null && !isDefined(e.id)) { message }
        }

        @JvmStatic
        fun isEntityDefined(e: Entity?, message: String) {
            check(e != null && isDefined(e.id)) { message }
        }
    }
}