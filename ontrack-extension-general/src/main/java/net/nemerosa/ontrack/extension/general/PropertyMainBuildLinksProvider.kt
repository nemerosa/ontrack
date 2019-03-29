package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.labels.MainBuildLinksProvider
import net.nemerosa.ontrack.model.labels.ProvidedMainBuildLinksConfig
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class PropertyMainBuildLinksProvider(
        private val propertyService: PropertyService
) : MainBuildLinksProvider {
    override fun getMainBuildLinksConfig(project: Project): ProvidedMainBuildLinksConfig {
        val property: MainBuildLinksProjectProperty? = propertyService.getProperty(project, MainBuildLinksProjectPropertyType::class.java).value
        return ProvidedMainBuildLinksConfig(
                labels = property?.labels ?: emptyList(),
                order = ProvidedMainBuildLinksConfig.PROJECT,
                override = property?.overrideGlobal ?: false
        )
    }
}