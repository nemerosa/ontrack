package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension

/**
 * Defines a list of entries in the user menu in the UI.
 */
interface UserMenuListExtension: Extension {

    /**
     * List of menu entries
     */
    val userMenuExtensions: List<UserMenuExtension>

}
