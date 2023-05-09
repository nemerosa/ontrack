package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.model.extension.Extension

/**
 * Extension which provides a link from a hook response record.
 */
interface HookInfoLinkExtension: Extension {

    /**
     * Unique ID for this extension
     */
    val id: String

}