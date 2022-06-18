package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest

/**
 * Sending events about auto versioning.
 */
interface AutoVersioningEventService {

    /**
     * Broadcasts an error event.
     */
    fun sendError(
        order: AutoVersioningOrder,
        message: String,
        error: Exception? = null,
        pr: SCMPullRequest? = null,
    )

    /**
     * Broadcasts the success of an autoversioning success
     */
    fun sendSuccess(
        order: AutoVersioningOrder,
        message: String,
        pr: SCMPullRequest,
    )

}