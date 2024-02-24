package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.stereotype.Component

/**
 * Displays the auto versioning configuration, if any, for a branch.
 */
@Component
class AutoVersioningConfigInformationExtension(
    extensionFeature: AutoVersioningExtensionFeature,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override val title: String = "Auto-versioning"

    override fun getInformation(entity: ProjectEntity): EntityInformation? =
        if (entity is Branch) {
            autoVersioningConfigurationService.getAutoVersioning(entity)?.let { config ->
                EntityInformation(
                    this,
                    mapOf(
                        "yaml" to AutoVersioningConfigParser.toYaml(config)
                    )
                )
            }
        } else {
            null
        }


}