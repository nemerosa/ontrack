package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Service

@Service
class PreviousPromotionConditionServiceImpl(
    private val propertyService: PropertyService,
    private val cachedSettingsService: CachedSettingsService,
) : PreviousPromotionConditionService {

    override fun resolvePreviousPromotionCondition(promotionLevel: PromotionLevel): PreviousPromotionConditionResolution {
        // Promotion level first, then branch, then project: the most specific configuration answers,
        // and the search STOPS at the first entity carrying the property, whichever way it answers
        val entities: List<ProjectEntity> = listOf(
            promotionLevel,
            promotionLevel.branch,
            promotionLevel.branch.project,
        )
        entities.forEach { entity ->
            val property = propertyService.getPropertyValue(entity, PreviousPromotionConditionPropertyType::class.java)
            if (property != null) {
                return PreviousPromotionConditionResolution(
                    required = property.previousPromotionRequired,
                    source = entity,
                )
            }
        }
        // The settings always answer - the provider defaults to `false` - so the cascade terminates
        // here rather than leaving "not set anywhere" as a third case for callers to handle
        val settings = cachedSettingsService.getCachedSettings(PreviousPromotionConditionSettings::class.java)
        return PreviousPromotionConditionResolution(
            required = settings.previousPromotionRequired,
            source = null,
        )
    }

}
