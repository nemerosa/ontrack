package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionLevelConfiguration
import net.nemerosa.ontrack.model.structure.PromotionLevelConfigurator
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class PromotionDependenciesPropertyTypeConfigurator(
    private val propertyService: PropertyService,
) : PromotionLevelConfigurator {

    override fun configure(pl: PromotionLevel, config: PromotionLevelConfiguration) {
        if (config.dependencies.isEmpty()) {
            propertyService.deleteProperty(pl, PromotionDependenciesPropertyType::class.java)
        } else {
            propertyService.editProperty(
                pl,
                PromotionDependenciesPropertyType::class.java,
                PromotionDependenciesProperty(dependencies = config.dependencies)
            )
        }
    }
}
