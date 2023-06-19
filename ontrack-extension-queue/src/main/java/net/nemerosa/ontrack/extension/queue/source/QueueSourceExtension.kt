package net.nemerosa.ontrack.extension.queue.source

import net.nemerosa.ontrack.model.extension.Extension

/**
 * Extension which provides a link to the source of a queue record.
 *
 * @param T Type of data provided for the [QueueSource.data].
 */
interface QueueSourceExtension<T> : Extension {

    /**
     * Unique ID for this extension
     */
    val id: String

}