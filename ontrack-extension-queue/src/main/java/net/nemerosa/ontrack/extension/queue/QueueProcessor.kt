package net.nemerosa.ontrack.extension.queue

interface QueueProcessor<T : Any> {

    /**
     * Unique ID
     */
    val id: String

    /**
     * Returns true if the processing must be done synchronously
     */
    val sync: Boolean get() = false

    /**
     * Gets a specific routing key for the payload.
     *
     * Returns null by default, to rely on the global settings
     */
    fun getSpecificRoutingKey(payload: T): String? = null

    /**
     * When there is no [specific routing key][getSpecificRoutingKey], this function
     * is called to return the identifier of the payload used to spread the load
     * among the available queues.
     */
    fun getRoutingIdentifier(payload: T): String

    /**
     * Processes the payload.
     */
    fun process(payload: T)

}