package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import java.util.*

@Component
class BitBucketProjectDecorator(
    extensionFeature: StashExtensionFeature,
    private val propertyService: PropertyService
) : AbstractExtension(extensionFeature), DecorationExtension<String> {

    override fun getDecorations(entity: ProjectEntity): List<Decoration<String>> {
        val property = propertyService.getPropertyValue(entity, StashProjectConfigurationPropertyType::class.java)
        return if (property != null) {
            listOf(
                Decoration.of(
                    this,
                    "${property.project}/${property.repository} @ ${property.configuration.name}"
                )
            )
        } else {
            emptyList()
        }
    }

    override fun getScope(): EnumSet<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.PROJECT)
    }
}
