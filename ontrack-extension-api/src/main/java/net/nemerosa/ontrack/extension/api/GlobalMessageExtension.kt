package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.message.Message

/**
 * Provides a list of global messages
 */
interface GlobalMessageExtension : Extension {

    /**
     * Gets a list of messages
     */
    val globalMessages: List<Message>

}