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
     * Processes the payload.
     */
    fun process(payload: T)

}