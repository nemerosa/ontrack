package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AutoPromotionPropertyTypeIT : AbstractDSLTestSupport() {

    @Test
    fun `Providing validation stamps IDs`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    val pl = promotionLevel()
                    propertyService.editProperty(
                        entity = pl,
                        propertyTypeName = AutoPromotionPropertyType::class.java.name,
                        data = mapOf(
                            "validationStamps" to listOf(
                                vs.id()
                            )
                        ).asJson()
                    )
                    assertNotNull(propertyService.getPropertyValue(pl, AutoPromotionPropertyType::class.java)) {
                        assertEquals(
                            listOf(vs),
                            it.validationStamps,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Providing promotion levels IDs`() {
        asAdmin {
            project {
                branch {
                    val previous = promotionLevel()
                    val pl = promotionLevel()
                    propertyService.editProperty(
                        entity = pl,
                        propertyTypeName = AutoPromotionPropertyType::class.java.name,
                        data = mapOf(
                            "promotionLevels" to listOf(
                                previous.id()
                            )
                        ).asJson()
                    )
                    assertNotNull(propertyService.getPropertyValue(pl, AutoPromotionPropertyType::class.java)) {
                        assertEquals(
                            listOf(previous),
                            it.promotionLevels,
                        )
                    }
                }
            }
        }
    }

}