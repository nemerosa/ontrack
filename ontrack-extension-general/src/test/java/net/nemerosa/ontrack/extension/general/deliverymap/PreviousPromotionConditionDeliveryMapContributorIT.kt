package net.nemerosa.ontrack.extension.general.deliverymap

import net.nemerosa.ontrack.extension.general.PreviousPromotionConditionProperty
import net.nemerosa.ontrack.extension.general.PreviousPromotionConditionPropertyType
import net.nemerosa.ontrack.extension.general.PreviousPromotionConditionSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapService
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

/**
 * The previous-promotion condition on the delivery map, through the whole cascade it is resolved by.
 *
 * The suppression of a *requires* shadowed by an *unlocks* is deliberately NOT tested here: it is a
 * general rule of `DeliveryMapServiceImpl` and is covered by that class's own test, so that it does
 * not read as a rule this feature owns.
 */
class PreviousPromotionConditionDeliveryMapContributorIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var deliveryMapService: DeliveryMapService

    @Test
    fun `Set on the promotion level, the condition requires the level below it`() {
        withPreviousPromotionGlobalCondition(false) {
            project {
                branch {
                    val bronze = promotionLevel()
                    val silver = promotionLevel()
                    silver.previousPromotionCondition(true)
                    assertRequires(bronze.id(), silver.id())
                }
            }
        }
    }

    @Test
    fun `Inherited from the branch, the condition draws the whole chain`() {
        withPreviousPromotionGlobalCondition(false) {
            project {
                branch {
                    previousPromotionCondition(true)
                    val bronze = promotionLevel()
                    val silver = promotionLevel()
                    val gold = promotionLevel()
                    assertRequires(bronze.id() to silver.id(), silver.id() to gold.id())
                }
            }
        }
    }

    @Test
    fun `Inherited from the project, the condition draws the chain`() {
        withPreviousPromotionGlobalCondition(false) {
            project {
                previousPromotionCondition(true)
                branch {
                    val bronze = promotionLevel()
                    val silver = promotionLevel()
                    assertRequires(bronze.id(), silver.id())
                }
            }
        }
    }

    @Test
    fun `Inherited from the global settings, the condition draws the chain`() {
        withPreviousPromotionGlobalCondition(true) {
            project {
                branch {
                    val bronze = promotionLevel()
                    val silver = promotionLevel()
                    assertRequires(bronze.id(), silver.id())
                }
            }
        }
    }

    @Test
    fun `A false lower in the cascade stops the search and draws nothing`() {
        withPreviousPromotionGlobalCondition(true) {
            project {
                previousPromotionCondition(true)
                branch {
                    promotionLevel()
                    val silver = promotionLevel()
                    silver.previousPromotionCondition(false)
                    assertNoEdge()
                }
            }
        }
    }

    @Test
    fun `A false on the branch overrides a true on the project`() {
        withPreviousPromotionGlobalCondition(true) {
            project {
                previousPromotionCondition(true)
                branch {
                    previousPromotionCondition(false)
                    promotionLevel()
                    promotionLevel()
                    assertNoEdge()
                }
            }
        }
    }

    @Test
    fun `The first promotion level of the order has no predecessor to require`() {
        withPreviousPromotionGlobalCondition(true) {
            project {
                branch {
                    promotionLevel()
                    // One level only: nothing below it, so the whole map has no edge at all
                    assertNoEdge()
                }
            }
        }
    }

    @Test
    fun `Nothing is drawn when the condition is off everywhere`() {
        withPreviousPromotionGlobalCondition(false) {
            project {
                branch {
                    promotionLevel()
                    promotionLevel()
                    assertNoEdge()
                }
            }
        }
    }

    private fun Branch.assertRequires(source: Int, target: Int) =
        assertRequires(source to target)

    private fun Branch.assertRequires(vararg pairs: Pair<Int, Int>) {
        assertEquals(
            pairs.map { (source, target) -> "requires:promotion-level:$source->promotion-level:$target" },
            deliveryMapService.getDeliveryMap(this).edges.map { it.id },
        )
    }

    private fun Branch.assertNoEdge() {
        assertEquals(emptyList(), deliveryMapService.getDeliveryMap(this).edges.map { it.id })
    }

    private fun <T : ProjectEntity> T.previousPromotionCondition(required: Boolean) {
        setProperty(
            this,
            PreviousPromotionConditionPropertyType::class.java,
            PreviousPromotionConditionProperty(previousPromotionRequired = required),
        )
    }

    private fun withPreviousPromotionGlobalCondition(required: Boolean, code: () -> Unit) {
        val previous = settingsService.getCachedSettings(PreviousPromotionConditionSettings::class.java)
        try {
            asUser().with(GlobalSettings::class.java).execute {
                settingsManagerService.saveSettings(
                    PreviousPromotionConditionSettings(previousPromotionRequired = required)
                )
            }
            asAdmin { code() }
        } finally {
            asUser().with(GlobalSettings::class.java).execute {
                settingsManagerService.saveSettings(previous)
            }
        }
    }

}
