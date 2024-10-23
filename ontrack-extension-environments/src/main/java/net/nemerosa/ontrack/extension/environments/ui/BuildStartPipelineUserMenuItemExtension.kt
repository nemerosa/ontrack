package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.api.ProjectEntityUserMenuItemExtension
import net.nemerosa.ontrack.extension.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extension.environments.security.SlotPipelineStart
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class BuildStartPipelineUserMenuItemExtension(
    extensionFeature: EnvironmentsExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(extensionFeature), ProjectEntityUserMenuItemExtension {

    override fun getItems(projectEntity: ProjectEntity): List<UserMenuItem> =
        if (projectEntity is Build &&
            securityService.isProjectFunctionGranted(
                projectEntity, SlotPipelineStart::class.java
            )
        ) {
            listOf(
                UserMenuItem(
                    groupId = CoreUserMenuGroups.INFORMATION,
                    extension = feature,
                    id = "startPipeline",
                    name = "Starts deployment pipeline",
                    local = true,
                    arguments = mapOf("buildId" to projectEntity.id()).asJson(),
                )
            )
        } else {
            emptyList()
        }
}