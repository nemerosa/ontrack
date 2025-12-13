package net.nemerosa.ontrack.extension.general.ci

import net.nemerosa.ontrack.extension.config.extensions.ProjectCIConfigExtension
import net.nemerosa.ontrack.extension.config.model.ProjectConfiguration
import net.nemerosa.ontrack.extension.general.*
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class AutoProjectCIConfigExtension(
    generalExtensionFeature: GeneralExtensionFeature,
    private val propertyService: PropertyService,
) : AbstractExtension(generalExtensionFeature), ProjectCIConfigExtension {

    override fun configureProject(
        project: Project,
        configuration: ProjectConfiguration
    ) {
        propertyService.editProperty(
            entity = project,
            propertyType = AutoValidationStampPropertyType::class.java,
            data = AutoValidationStampProperty(
                isAutoCreate = true,
                isAutoCreateIfNotPredefined = true,
            )
        )
        propertyService.editProperty(
            entity = project,
            propertyType = AutoPromotionLevelPropertyType::class.java,
            data = AutoPromotionLevelProperty(
                isAutoCreate = true,
            )
        )
    }
}