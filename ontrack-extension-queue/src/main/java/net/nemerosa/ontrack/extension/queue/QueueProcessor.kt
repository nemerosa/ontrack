package net.nemerosa.ontrack.extension.queue

interface QueueProcessor<T : Any> {

    /**
     * Processes the payload.
     */
    fun process(payload: T)

}