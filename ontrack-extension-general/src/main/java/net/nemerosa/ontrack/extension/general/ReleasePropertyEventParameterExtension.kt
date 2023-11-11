package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.EventParameterExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class ReleasePropertyEventParameterExtension(
    extensionFeature: GeneralExtensionFeature,
    private val propertyService: PropertyService,
) : AbstractExtension(extensionFeature), EventParameterExtension {

    override fun additionalTemplateParameters(entity: ProjectEntity): Map<String, String> =
        if (entity is Build) {
            val property = propertyService.getPropertyValue(entity, ReleasePropertyType::class.java)
            if (property != null) {
                mapOf(
                    "buildLabel" to property.name
                )
            } else {
                emptyMap()
            }
        } else {
            emptyMap()
        }

}