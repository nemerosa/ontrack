package net.nemerosa.ontrack.extension.gitlab

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.gitlab.property.GitLabProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import java.util.*

@Component
class GitLabProjectDecorator(
    extensionFeature: GitLabExtensionFeature,
    private val propertyService: PropertyService
) : AbstractExtension(extensionFeature), DecorationExtension<String> {

    override fun getDecorations(entity: ProjectEntity): List<Decoration<String>> {
        val property = propertyService.getPropertyValue(entity, GitLabProjectConfigurationPropertyType::class.java)
        if (property != null) {
            return listOf(
                Decoration.of(
                    this,
                    "${property.repository} @ ${property.configuration.name}"
                )
            )
        } else {
            return emptyList()
        }
    }

    override fun getScope(): EnumSet<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.PROJECT)
    }
}
