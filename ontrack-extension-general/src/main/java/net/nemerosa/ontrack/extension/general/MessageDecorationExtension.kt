package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageDecorationExtension(
    extensionFeature: GeneralExtensionFeature,
    private val propertyService: PropertyService
) : AbstractExtension(extensionFeature), DecorationExtension<MessageProperty> {

    override fun getScope(): EnumSet<ProjectEntityType> {
        return EnumSet.allOf(ProjectEntityType::class.java)
    }

    override fun getDecorations(entity: ProjectEntity): List<Decoration<MessageProperty>> {
        return propertyService.getPropertyValue<MessageProperty>(entity, MessagePropertyType::class.java)
            ?.let {
                listOf(
                    Decoration.of(this, it)
                )
            }
            ?: emptyList()
    }
}
