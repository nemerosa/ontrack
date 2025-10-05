package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.config.model.PromotionLevelConfiguration
import net.nemerosa.ontrack.extension.config.model.PromotionLevelConfigurator
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.setupValidationStamp
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class AutoPromotionPropertyTypeConfigurator(
    private val propertyService: PropertyService,
    private val structureService: StructureService,
) : PromotionLevelConfigurator {
    override fun configure(
        pl: PromotionLevel,
        config: PromotionLevelConfiguration
    ) {
        if (config.validations.isEmpty() && config.promotions.isEmpty()) {
            propertyService.deleteProperty(pl, AutoPromotionPropertyType::class.java)
        } else {
            propertyService.editProperty(
                pl,
                AutoPromotionPropertyType::class.java,
                AutoPromotionProperty(
                    validationStamps = config.validations.map { name ->
                        structureService.setupValidationStamp(pl.branch, name, "")
                    },
                    promotionLevels = config.promotions.mapNotNull { name ->
                        structureService.findPromotionLevelByName(pl.project.name, pl.branch.name, name)
                            .getOrNull()
                    },
                    include = "",
                    exclude = ""
                )
            )
        }
    }
}