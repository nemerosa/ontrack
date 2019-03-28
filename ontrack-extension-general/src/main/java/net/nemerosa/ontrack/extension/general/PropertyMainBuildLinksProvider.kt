package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import net.nemerosa.ontrack.model.labels.MainBuildLinksProvider
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class PropertyMainBuildLinksProvider(
        private val propertyService: PropertyService
) : MainBuildLinksProvider {
    override fun getMainBuildLinksConfig(project: Project): MainBuildLinksConfig {
        return propertyService.getProperty(project, MainBuildLinksProjectPropertyType::class.java).value
                ?.let { MainBuildLinksConfig(it.labels) }
                ?: MainBuildLinksConfig(emptyList())
    }
}