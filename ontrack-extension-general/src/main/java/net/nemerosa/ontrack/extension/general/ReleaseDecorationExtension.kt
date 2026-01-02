package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReleaseDecorationExtension(
    extensionFeature: GeneralExtensionFeature,
    private val propertyService: PropertyService
) : AbstractExtension(extensionFeature), DecorationExtension<String> {

    override fun getScope(): EnumSet<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.BUILD)
    }

    override fun getDecorations(entity: ProjectEntity): List<Decoration<String>> {
        // Argument check
        check(entity is Build) { "Expecting build" }
        // Gets the `release` property
        return propertyService.getPropertyValue(entity, ReleasePropertyType::class.java)
            ?.let {
                listOf(
                    Decoration.of(this, it.name)
                )
            }
            ?: emptyList()
    }
}
