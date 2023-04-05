package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.security.GlobalFunction

/**
 * Extension linked to a global role.
 */
interface GlobalExtension : Extension {
    /**
     * Global function which must be granted for the extension to
     * be available.
     *
     * It can be null: in this case, anybody is allowed access to this extension.
     */
    val globalFunction: Class<out GlobalFunction>?
}
