package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class GitHubProjectDecorator(
    extensionFeature: GitHubExtensionFeature,
    private val propertyService: PropertyService
) : AbstractExtension(extensionFeature), DecorationExtension<GitHubProjectDecoratorData> {
    override fun getDecorations(entity: ProjectEntity): List<Decoration<GitHubProjectDecoratorData>> {
        val property = propertyService.getPropertyValue(
            entity,
            GitHubProjectConfigurationPropertyType::class.java
        )
        return if (property != null) {
            listOf(
                Decoration.of(
                    this,
                    GitHubProjectDecoratorData(
                        displayText = "${property.repository} @ ${property.configuration.name}",
                        url = property.url
                    )
                )
            )
        } else {
            emptyList()
        }
    }

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.of(ProjectEntityType.PROJECT)

}
