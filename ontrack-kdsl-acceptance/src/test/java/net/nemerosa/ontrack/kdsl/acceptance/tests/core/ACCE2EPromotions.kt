package net.nemerosa.ontrack.kdsl.acceptance.tests.core

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.PromotionLevel
import net.nemerosa.ontrack.kdsl.spec.charts.getChart
import org.junit.jupiter.api.Test

class ACCE2EPromotions: AbstractACCDSLTestSupport() {

    @Test
    fun `End to end promotion data`() {
        end2endPromotionData { refPromotion, targetPromotion ->

            val chart = ontrack.getChart(
                name = "e2e-lead-time",
                interval = "1w",
                period = "1d",
                parameters = mapOf(
                    "refPromotionId" to refPromotion.id,
                    "samePromotion" to false,
                    "targetPromotionId" to targetPromotion.id,
                    "targetProject" to targetPromotion.branch.project.name,
                    "maxDepth" to 5,
                ).asJson()
            )

            println(chart)

        }
    }

    private fun end2endPromotionData(
        code: (refPromotion: PromotionLevel, targetPromotion: PromotionLevel) -> Unit
    ) {

        // Reference time
        val ref = Time.now().minusHours(8)

        lateinit var refPromotion: PromotionLevel
        lateinit var targetPromotion: PromotionLevel

        // Module
        project("module", deleteFirst = true) {
            branch("main") {
                refPromotion = promotion(ACCBuildLinks.GOLD)
                build("1") {
                    updateCreationTime(ref)
                    promote(ACCBuildLinks.GOLD, dateTime = ref)
                }
                build("2") {
                    updateCreationTime(ref.plusMinutes(180))
                }
                build("3") {
                    updateCreationTime(ref.plusMinutes(240))
                }
                build("4") {
                    updateCreationTime(ref.plusMinutes(300))
                    promote(ACCBuildLinks.GOLD, dateTime = ref.plusMinutes(300))
                }
            }
        }

        // Application
        project("application", deleteFirst = true) {
            branch("main") {
                promotion(ACCBuildLinks.GOLD)
                build("1") {
                    updateCreationTime(ref.plusMinutes(60))
                    linksTo("module" to "1")
                    promote(ACCBuildLinks.GOLD, dateTime = ref.plusMinutes(60))
                }
                build("2") {
                    updateCreationTime(ref.plusMinutes(210))
                    linksTo("module" to "2")
                }
                build("3") {
                    updateCreationTime(ref.plusMinutes(270))
                    linksTo("module" to "2")
                }
                build("4") {
                    updateCreationTime(ref.plusMinutes(315))
                    promote(ACCBuildLinks.GOLD, dateTime = ref.plusMinutes(315))
                    linksTo("module" to "4")
                }
            }
        }

        // Aggregator
        project("aggregator", deleteFirst = true) {
            branch("main") {
                targetPromotion = promotion(ACCBuildLinks.SILVER)
                build("1") {
                    updateCreationTime(ref.plusMinutes(120))
                    linksTo("application" to "1")
                    promote(ACCBuildLinks.SILVER, dateTime = ref.plusMinutes(120))
                }
                build("2") {
                    updateCreationTime(ref.plusMinutes(270))
                    linksTo("application" to "2")
                }
                build("3") {
                    updateCreationTime(ref.plusMinutes(300))
                    linksTo("application" to "2")
                }
                build("4") {
                    updateCreationTime(ref.plusMinutes(330))
                    promote(ACCBuildLinks.SILVER, dateTime = ref.plusMinutes(330))
                    linksTo("application" to "4")
                }
            }
        }

        // OK
        code(
            refPromotion,
            targetPromotion,
        )
    }

}