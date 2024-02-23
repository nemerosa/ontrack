package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.UserMenuItem

/**
 * Contributes a list of menu items for a project entity.
 */
interface ProjectEntityUserMenuItemExtension : Extension {

    /**
     * Returns a list of menu entries.
     *
     * @param projectEntity Entity to contribute to
     * @return List of menu entries or empty if not applicable
     */
    fun getItems(projectEntity: ProjectEntity): List<UserMenuItem>

}