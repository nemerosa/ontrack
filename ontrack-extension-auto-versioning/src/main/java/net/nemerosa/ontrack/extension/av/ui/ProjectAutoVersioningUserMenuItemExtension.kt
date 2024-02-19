package net.nemerosa.ontrack.extension.av.ui

import net.nemerosa.ontrack.extension.api.ProjectEntityUserMenuItemExtension
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class ProjectAutoVersioningUserMenuItemExtension(
    private val autoVersioningExtensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(autoVersioningExtensionFeature), ProjectEntityUserMenuItemExtension {

    override fun getItems(projectEntity: ProjectEntity): List<UserMenuItem> =
        if (projectEntity is Project) {
            listOf(
                UserMenuItem(
                    groupId = CoreUserMenuGroups.INFORMATION,
                    extension = autoVersioningExtensionFeature.id,
                    id = "audit-project-target/${projectEntity.id}",
                    name = "Auto versioning audit (target)",
                ),
                UserMenuItem(
                    groupId = CoreUserMenuGroups.INFORMATION,
                    extension = autoVersioningExtensionFeature.id,
                    id = "audit-project-source/${projectEntity.id}",
                    name = "Auto versioning audit (source)",
                ),
            )
        } else {
            emptyList()
        }

}