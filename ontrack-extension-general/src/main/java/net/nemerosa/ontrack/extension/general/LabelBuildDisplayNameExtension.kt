package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.BuildDisplayNameExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class LabelBuildDisplayNameExtension(
        extensionFeature: GeneralExtensionFeature,
        private val propertyService: PropertyService
) : AbstractExtension(extensionFeature), BuildDisplayNameExtension {
    override fun getBuildDisplayName(build: Build): String? {
        val displayProperty: BuildLinkDisplayProperty? = propertyService.getProperty(build.project, BuildLinkDisplayPropertyType::class.java).value
        val labelProperty: ReleaseProperty? = propertyService.getProperty(build, ReleasePropertyType::class.java).value
        return displayProperty.getLabel(build, labelProperty)
    }
}