package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

class ACCDSLPromotionDependenciesCheck extends AbstractACCDSL {

    @Test
    void 'Promotion dependency property set to null when not defined'() {
        String projectName = uid("P")
        ontrack.project(projectName) {
            branch("master") {
                def pl = promotionLevel("PLATINUM")
                assert pl.config.promotionDependencies == null
            }
        }
    }

    @Test
    void 'Promotion dependency property'() {
        String projectName = uid("P")
        ontrack.project(projectName) {
            branch("master") {
                def pl = promotionLevel("PLATINUM")
                pl.config.promotionDependencies = ["SILVER", "GOLD"]
                assert pl.config.promotionDependencies == ["SILVER", "GOLD"]
            }
        }
    }

    @Test
    void 'Promotion dependency missing'() {
        String projectName = uid("P")
        ontrack.project(projectName) {
            branch("master") {
                promotionLevel "IRON"
                promotionLevel "SILVER"
                promotionLevel "GOLD"
                promotionLevel("PLATINUM").config.promotionDependencies = ["SILVER", "GOLD"]
                build("1") {
                    promote("SILVER")
                    assertFailsWithMessage("The \"Promotion Dependencies Condition\" setup in PLATINUM prevents\n" +
                            "the Build $projectName/master/1 to be promoted because it requires the following promotions\n" +
                            "to be all granted (SILVER,GOLD) and \"GOLD\" was not granted.") {
                        promote "PLATINUM"
                    }
                }
            }
        }
    }

    @Test
    void 'Promotion dependency check OK'() {
        String projectName = uid("P")
        ontrack.project(projectName) {
            branch("master") {
                promotionLevel "IRON"
                promotionLevel "SILVER"
                promotionLevel "GOLD"
                promotionLevel("PLATINUM").config.promotionDependencies = ["SILVER", "GOLD"]
                build("1") {
                    promote("SILVER")
                    promote("GOLD")
                    promote("PLATINUM")
                }
            }
        }
    }

}
