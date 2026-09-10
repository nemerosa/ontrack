package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Service

@Service
class PreviousPromotionConditionServiceImpl(
    private val propertyService: PropertyService,
    private val cachedSettingsService: CachedSettingsService,
) : PreviousPromotionConditionService {

    override fun resolvePreviousPromotionCondition(promotionLevel: PromotionLevel): PreviousPromotionConditionResolution =
        resolve(promotionLevel, ::loadProperty)

    override fun resolvePreviousPromotionConditions(
        promotionLevels: List<PromotionLevel>,
    ): Map<ID, PreviousPromotionConditionResolution> {
        val memo = PropertyMemo()
        return promotionLevels.associate { promotionLevel ->
            promotionLevel.id to resolve(promotionLevel, memo::get)
        }
    }

    /**
     * The cascade itself, over whatever [propertyOf] reads the property with: straight through to the
     * repository for one promotion level, memoised for a batch. Written once so that the two entry
     * points cannot answer differently - which is the same reason this class exists at all.
     */
    private fun resolve(
        promotionLevel: PromotionLevel,
        propertyOf: (ProjectEntity) -> PreviousPromotionConditionProperty?,
    ): PreviousPromotionConditionResolution {
        // Promotion level first, then branch, then project: the most specific configuration answers,
        // and the search STOPS at the first entity carrying the property, whichever way it answers
        val entities: List<ProjectEntity> = listOf(
            promotionLevel,
            promotionLevel.branch,
            promotionLevel.branch.project,
        )
        entities.forEach { entity ->
            val property = propertyOf(entity)
            if (property != null) {
                return PreviousPromotionConditionResolution(
                    required = property.previousPromotionRequired,
                    source = entity,
                )
            }
        }
        // The settings always answer - the provider defaults to `false` - so the cascade terminates
        // here rather than leaving "not set anywhere" as a third case for callers to handle. Reading
        // them per promotion level costs nothing: `CachedSettingsService` is the cache.
        val settings = cachedSettingsService.getCachedSettings(PreviousPromotionConditionSettings::class.java)
        return PreviousPromotionConditionResolution(
            required = settings.previousPromotionRequired,
            source = null,
        )
    }

    private fun loadProperty(entity: ProjectEntity): PreviousPromotionConditionProperty? =
        propertyService.getPropertyValue(entity, PreviousPromotionConditionPropertyType::class.java)

    /**
     * One entity, one read, for the length of one batch.
     *
     * `getOrPut` would be the obvious way to write this and would be wrong: it recomputes whenever
     * the stored value is null, and *absent* is by far the most common answer here - a branch with no
     * property is exactly the case the batch exists to stop re-querying. Hence the explicit
     * containsKey, which distinguishes "read it, there is nothing" from "never read it".
     *
     * Keyed by type AND id because the id spaces of the entity types are separate: branch 12 and
     * project 12 are different entities.
     */
    private inner class PropertyMemo {

        private val read = mutableMapOf<Pair<ProjectEntityType, Int>, PreviousPromotionConditionProperty?>()

        fun get(entity: ProjectEntity): PreviousPromotionConditionProperty? {
            val key = entity.projectEntityType to entity.id()
            return if (key in read) {
                read[key]
            } else {
                loadProperty(entity).also { read[key] = it }
            }
        }
    }

}
