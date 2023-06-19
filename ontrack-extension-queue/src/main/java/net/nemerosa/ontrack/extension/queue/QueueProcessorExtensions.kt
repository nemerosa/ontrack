package net.nemerosa.ontrack.extension.queue

/**
 * Queue prefix for the processor
 */
val QueueProcessor<*>.queueNamePrefix: String
    get() =
        "ontrack.queue.$id"


/**
 * Queue routing prefix for the processor
 */
val QueueProcessor<*>.queueRoutingPrefix: String
    get() =
        "${queueNamePrefix}.routing"
