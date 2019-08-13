package net.nemerosa.ontrack.extension.general

import org.junit.Test

class PreviousPromotionConditionPropertyCheckIT : AbstractGeneralExtensionTestSupport() {

    @Test
    fun `No promotion check`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    private fun withPreviousPromotionGlobalCondition(static: Boolean = false, settings: Boolean = false, code: () -> Unit) {
        code()
    }

}
