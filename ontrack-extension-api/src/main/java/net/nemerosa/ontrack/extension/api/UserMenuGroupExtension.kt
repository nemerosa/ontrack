package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.support.UserMenuGroup

/**
 * Contributes a list of menu groups for the current user.
 */
interface UserMenuGroupExtension : Extension {

    val groups: List<UserMenuGroup>

}