package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.support.UserMenuItem

/**
 * Contributes a list of menu items for the current user.
 */
interface UserMenuItemExtension: Extension {

    val items: List<UserMenuItem>

}