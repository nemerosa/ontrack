package net.nemerosa.ontrack.extension.environments.promotions

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.settings.EnvironmentsSettingsBuildDisplayOption
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EnvironmentsBuildPromotionInfoExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentsBuildPromotionInfoExtensionTestSupport: EnvironmentsBuildPromotionInfoExtensionTestSupport

    @Test
    fun `Getting the promotion info for a build with display option being ALL`() {
        environmentsBuildPromotionInfoExtensionTestSupport.withSetup(
            buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.ALL
        ) { test ->

            val (
                _,
                info,
                bronze,
                silver,
                gold,
                runBronze1,
                runBronze2,
                runSilver,
                _,
                eligibleSlotWithNoPromotionRulePipeline,
                _,
                eligibleSlotWithSilverPromotionRulePipeline,
            ) = test

            // Checking all items have been collected
            assertEquals(6, info.items.size)
            var index = 0

            // First, the slots & NOT their pipelines

            info.items[index++].apply {
                assertNull(promotionLevel)
                assertEquals(eligibleSlotWithSilverPromotionRulePipeline.id, (data as SlotPipeline).id)
            }

            info.items[index++].apply {
                assertNull(promotionLevel)
                assertEquals(eligibleSlotWithNoPromotionRulePipeline.id, (data as SlotPipeline).id)
            }

            // Then the promotions & their promotion runs

            info.items[index++].apply {
                assertEquals(gold, promotionLevel)
                assertEquals(gold, data)
            }

            info.items[index++].apply {
                assertEquals(silver, promotionLevel)
                assertEquals(runSilver.id, (data as PromotionRun).id)
            }

            info.items[index++].apply {
                assertEquals(bronze, promotionLevel)
                assertEquals(runBronze2.id, (data as PromotionRun).id)
            }

            info.items[index].apply {
                assertEquals(bronze, promotionLevel)
                assertEquals(runBronze1.id, (data as PromotionRun).id)
            }
        }
    }

    @Test
    fun `Getting the promotion info for a build with display option being HIGHEST`() {
        environmentsBuildPromotionInfoExtensionTestSupport.withSetup(
            buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.HIGHEST
        ) { test ->

            val (
                _,
                info,
                bronze,
                silver,
                gold,
                runBronze1,
                runBronze2,
                runSilver,
                _,
                _,
                _,
                eligibleSlotWithSilverPromotionRulePipeline,
            ) = test


            // Checking all items have been collected
            assertEquals(5, info.items.size)
            var index = 0

            // First, the slot where the build is deployed

            info.items[index++].apply {
                assertEquals(null, promotionLevel)
                assertEquals(eligibleSlotWithSilverPromotionRulePipeline.id, (data as SlotPipeline).id)
            }

            // Then the promotions & their promotion runs

            info.items[index++].apply {
                assertEquals(gold, promotionLevel)
                assertEquals(gold, data)
            }

            info.items[index++].apply {
                assertEquals(silver, promotionLevel)
                assertEquals(runSilver.id, (data as PromotionRun).id)
            }

            info.items[index++].apply {
                assertEquals(bronze, promotionLevel)
                assertEquals(runBronze2.id, (data as PromotionRun).id)
            }

            info.items[index].apply {
                assertEquals(bronze, promotionLevel)
                assertEquals(runBronze1.id, (data as PromotionRun).id)
            }
        }
    }

    @Test
    fun `Getting the promotion info for a build with display option being COUNT`() {
        environmentsBuildPromotionInfoExtensionTestSupport.withSetup(
            buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.COUNT
        ) { test ->

            val (
                build,
                info,
                bronze,
                silver,
                gold,
                runBronze1,
                runBronze2,
                runSilver,
                _,
                _,
                _,
                _,
            ) = test


            // Checking all items have been collected
            assertEquals(5, info.items.size)
            var index = 0

            // First, the count of slots where the build is deployed

            info.items[index++].apply {
                assertEquals(null, promotionLevel)
                assertEquals(EnvironmentBuildCount(build, count = 2), data)
            }

            // Then the promotions & their promotion runs

            info.items[index++].apply {
                assertEquals(gold, promotionLevel)
                assertEquals(gold, data)
            }

            info.items[index++].apply {
                assertEquals(silver, promotionLevel)
                assertEquals(runSilver.id, (data as PromotionRun).id)
            }

            info.items[index++].apply {
                assertEquals(bronze, promotionLevel)
                assertEquals(runBronze2.id, (data as PromotionRun).id)
            }

            info.items[index].apply {
                assertEquals(bronze, promotionLevel)
                assertEquals(runBronze1.id, (data as PromotionRun).id)
            }
        }
    }

}