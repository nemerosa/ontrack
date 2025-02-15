package net.nemerosa.ontrack.model.trigger

/**
 * Registry of all triggers
 */
interface TriggerRegistry {

    /**
     * Getting the list of triggers, ordered by display name
     */
    val triggers: List<Trigger<*>>

    /**
     * Getting a trigger by its ID or null if not found
     */
    fun <T> findTriggerById(id: String): Trigger<T>?

}