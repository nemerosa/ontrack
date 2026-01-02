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
class AutoPromotionPropertyDecorator(
    extensionFeature: GeneralExtensionFeature,
    private val propertyService: PropertyService
) : AbstractExtension(extensionFeature), DecorationExtension<Boolean> {

    override fun getScope(): EnumSet<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.PROMOTION_LEVEL)
    }

    override fun getDecorations(entity: ProjectEntity): List<Decoration<Boolean>> {
        return propertyService.getPropertyValue(entity, AutoPromotionPropertyType::class.java)
            ?.let {
                listOf(
                    Decoration.of(this, true)
                )
            }
            ?: emptyList()
    }
}
