package net.nemerosa.ontrack.extension.environments.promotions

import net.nemerosa.ontrack.extension.api.BuildPromotionInfoExtension
import net.nemerosa.ontrack.extension.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.settings.EnvironmentsSettings
import net.nemerosa.ontrack.extension.environments.settings.EnvironmentsSettingsBuildDisplayOption
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EnvironmentsBuildPromotionInfoExtension(
    extensionFeature: EnvironmentsExtensionFeature,
    private val cachedSettingsService: CachedSettingsService,
    private val slotService: SlotService,
) : AbstractExtension(extensionFeature), BuildPromotionInfoExtension {

    override val types: Collection<KClass<*>> = setOf(
        SlotPipeline::class,
        EnvironmentBuildCount::class,
    )

    override fun buildPromotionInfoItems(
        items: MutableList<BuildPromotionInfoItem<*>>,
        build: Build,
        promotionLevels: List<PromotionLevel>
    ) {
        val (buildDisplayOption) = cachedSettingsService.getCachedSettings(EnvironmentsSettings::class.java)
        return when (buildDisplayOption) {
            EnvironmentsSettingsBuildDisplayOption.ALL -> allEnvironments(items, build)
            EnvironmentsSettingsBuildDisplayOption.HIGHEST -> highestEnvironment(items, build)
            EnvironmentsSettingsBuildDisplayOption.COUNT -> countEnvironments(items, build)
        }
    }

    private fun countEnvironments(
        items: MutableList<BuildPromotionInfoItem<*>>,
        build: Build
    ) {
        val count = slotService.findSlotPipelinesWhereBuildIsLastDeployed(build).size
        items.add(0, buildPromotionInfoItemForDeployedSlotCount(build, count))
    }

    private fun highestEnvironment(
        items: MutableList<BuildPromotionInfoItem<*>>,
        build: Build
    ) {
        val slotPipeline = slotService.findSlotPipelinesWhereBuildIsLastDeployed(build).firstOrNull()
        if (slotPipeline != null) {
            items.add(0, buildPromotionInfoItemForDeployedSlotPipeline(slotPipeline))
        }
    }

    private fun allEnvironments(
        items: MutableList<BuildPromotionInfoItem<*>>,
        build: Build
    ) {
        val slotPipelines = slotService.findSlotPipelinesWhereBuildIsLastDeployed(build)
        items.addAll(0, slotPipelines.map { slotPipeline ->
            buildPromotionInfoItemForDeployedSlotPipeline(slotPipeline)
        })
    }

    private fun buildPromotionInfoItemForDeployedSlotPipeline(
        slotPipeline: SlotPipeline,
    ) =
        BuildPromotionInfoItem(
            promotionLevel = null,
            data = slotPipeline,
        )

    private fun buildPromotionInfoItemForDeployedSlotCount(build: Build, count: Int) =
        BuildPromotionInfoItem(
            promotionLevel = null,
            data = EnvironmentBuildCount(build, count),
        )

}