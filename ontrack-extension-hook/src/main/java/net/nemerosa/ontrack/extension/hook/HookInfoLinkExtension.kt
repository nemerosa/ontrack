package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.model.extension.Extension

/**
 * Extension which provides a link from a hook response record.
 *
 * @param T Type of data provided for the [HookInfoLink.data].
 */
interface HookInfoLinkExtension<T> : Extension {

    /**
     * Unique ID for this extension
     */
    val id: String

}