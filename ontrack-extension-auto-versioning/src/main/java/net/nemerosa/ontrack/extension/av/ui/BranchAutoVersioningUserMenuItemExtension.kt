package net.nemerosa.ontrack.extension.av.ui

import net.nemerosa.ontrack.extension.api.ProjectEntityUserMenuItemExtension
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class BranchAutoVersioningUserMenuItemExtension(
    private val autoVersioningExtensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(autoVersioningExtensionFeature), ProjectEntityUserMenuItemExtension {

    override fun getItems(projectEntity: ProjectEntity): List<UserMenuItem> =
        if (projectEntity is Branch) {
            listOf(
                UserMenuItem(
                    groupId = CoreUserMenuGroups.INFORMATION,
                    extension = autoVersioningExtensionFeature,
                    id = "audit-branch-target/${projectEntity.id}",
                    name = "Auto versioning audit",
                ),
                UserMenuItem(
                    groupId = CoreUserMenuGroups.INFORMATION,
                    extension = autoVersioningExtensionFeature,
                    id = "config/${projectEntity.id}",
                    name = "Auto versioning configuration",
                ),
            )
        } else {
            emptyList()
        }

}