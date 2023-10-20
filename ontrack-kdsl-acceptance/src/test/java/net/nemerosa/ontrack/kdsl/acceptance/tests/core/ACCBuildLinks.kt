package net.nemerosa.ontrack.kdsl.acceptance.tests.core

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.admin.admin
import org.junit.jupiter.api.Test

class ACCBuildLinks : AbstractACCDSLTestSupport() {

    companion object {
        const val BRONZE = "BRONZE"
        const val SILVER = "SILVER"
        const val GOLD = "GOLD"
    }

    @Test
    fun `Build links example`() {
        buildLinkExample()
    }

    private fun buildLinkExample() {

        // Predefined promotions
        ontrack.admin.predefinedPromotionLevels.apply {
            createPredefinedPromotionLevel(
                name = BRONZE,
                image = ACCBuildLinks::class.java.getResource("/promotions/bronze.png")
            )
            createPredefinedPromotionLevel(
                name = SILVER,
                image = ACCBuildLinks::class.java.getResource("/promotions/silver.png")
            )
            createPredefinedPromotionLevel(
                name = GOLD,
                image = ACCBuildLinks::class.java.getResource("/promotions/gold.png")
            )
        }

        project("module-one", deleteFirst = true) {
            branch("release-1.23") {
                promotion(BRONZE)
                build("1.23.0") {
                    promote(BRONZE)
                }
                build("1.23.1") {

                }
                build("1.23.2") {
                    promote(BRONZE)
                }
            }
        }

        project("module-two", deleteFirst = true) {
            branch("release-1.23") {
                promotion(BRONZE)
                build("1.23.0") {
                    linksTo(
                        "module-one" to "1.23.0",
                    )
                }
                build("1.23.1") {
                    promote(BRONZE)
                    linksTo(
                        "module-one" to "1.23.2",
                    )
                }
            }
        }

        project("application", deleteFirst = true) {
            branch("main") {
                promotion(BRONZE)
                promotion(SILVER)
                build("2.0.1") {
                    promote(BRONZE)
                    linksTo(
                        "module-one" to "1.23.0",
                        "module-two" to "1.23.1",
                    )
                }
                build("2.0.2") {
                    promote(BRONZE)
                    promote(SILVER)
                    linksTo(
                        "module-one" to "1.23.2",
                        "module-two" to "1.23.1",
                    )
                }
            }
        }

        project("aggregator", deleteFirst = true) {
            branch("main") {
                promotion(BRONZE)
                promotion(SILVER)
                promotion(GOLD)
                build("1.0.119") {
                    promote(BRONZE)
                    linksTo(
                        "application" to "2.0.1",
                    )
                }
                build("1.0.120") {
                    promote(BRONZE)
                    promote(SILVER)
                    linksTo(
                        "application" to "2.0.2",
                    )
                }
                build("1.0.121") {
                    promote(BRONZE)
                    promote(SILVER)
                    linksTo(
                        "application" to "2.0.2",
                    )
                }
                build("1.0.122") {
                    promote(BRONZE)
                    promote(SILVER)
                    promote(GOLD)
                    linksTo(
                        "application" to "2.0.2",
                    )
                }
            }
        }

        project("prod-deploy", deleteFirst = true) {
            branch("main") {
                build("202311011600") {
                    linksTo(
                        "aggregator@live" to "1.0.119",
                        "aggregator@preview" to "1.0.120",
                    )
                }
                build("202311011751") {
                    linksTo(
                        "aggregator@live" to "1.0.120",
                        "aggregator@preview" to "1.0.120",
                    )
                }
                build("202311031345") {
                    linksTo(
                        "aggregator@live" to "1.0.120",
                        "aggregator@preview" to "1.0.121",
                    )
                }
                build("202311041017") {
                    linksTo(
                        "aggregator@live" to "1.0.120",
                        "aggregator@preview" to "1.0.122",
                    )
                }
            }
        }
    }

}