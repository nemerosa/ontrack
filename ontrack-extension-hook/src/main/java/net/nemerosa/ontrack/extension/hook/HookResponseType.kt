package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Types of responses returned by hooks.
 */
@APIDescription("Types of responses returned by hooks.")
enum class HookResponseType {

    /**
     * The hook payload was received but its content was not acceptable.
     */
    IGNORED,

    /**
     * The hook payload was received but its content was fully processed.
     */
    PROCESSED,

    /**
     * The hook payload was received and its content is processed in the background.
     */
    PROCESSING

}
