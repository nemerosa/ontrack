package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.client.ClientException
import net.nemerosa.ontrack.client.ClientValidationException
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

class ACCDSLPreviousPromotionCheck extends AbstractACCDSL {

    @Test
    void 'Previous promotion required at settings level'() {
        String projectName = uid("P")
        def old = ontrack.config.previousPromotionRequired
        try {
            ontrack.config.previousPromotionRequired = true
            ontrack.project(projectName) {
                branch("master") {
                    promotionLevel "IRON"
                    promotionLevel "SILVER"
                    build("1") {
                        assertFailsWithMessage("The \"Previous Promotion Condition\" settings prevent\n" +
                                "the SILVER to be granted because the IRON promotion\n" +
                                "has not been granted.") {
                            promote "SILVER"
                        }
                    }
                }
            }
        } finally {
            ontrack.config.previousPromotionRequired = old
        }
    }

    @Test
    void 'Previous promotion required at project level'() {
        String projectName = uid("P")
        ontrack.project(projectName) {
            assert config.previousPromotionRequired == null
            config.previousPromotionRequired = true
            assert config.previousPromotionRequired == true
            branch("master") {
                promotionLevel "IRON"
                promotionLevel "SILVER"
                build("1") {
                    assertFailsWithMessage("The \"Previous Promotion Condition\" setup in Project ${projectName} prevents\n" +
                            "the SILVER to be granted because the IRON promotion\n" +
                            "has not been granted.") {
                        promote "SILVER"
                    }
                }
            }
        }
    }

    @Test
    void 'Previous promotion required at branch level'() {
        String projectName = uid("P")
        ontrack.project(projectName) {
            branch("master") {
                assert config.previousPromotionRequired == null
                config.previousPromotionRequired = true
                assert config.previousPromotionRequired == true
                promotionLevel "IRON"
                promotionLevel "SILVER"
                build("1") {
                    assertFailsWithMessage("The \"Previous Promotion Condition\" setup in Branch ${projectName}/master prevents\n" +
                            "the SILVER to be granted because the IRON promotion\n" +
                            "has not been granted.") {
                        promote "SILVER"
                    }
                }
            }
        }
    }

    @Test
    void 'Previous promotion required at promotion level level'() {
        String projectName = uid("P")
        ontrack.project(projectName) {
            branch("master") {
                promotionLevel "IRON"
                def pl = promotionLevel("SILVER")
                assert pl.config.previousPromotionRequired == null
                pl.config.previousPromotionRequired = true
                assert pl.config.previousPromotionRequired == true
                build("1") {
                    assertFailsWithMessage("The \"Previous Promotion Condition\" setup in Promotion level ${projectName}/master/SILVER prevents\n" +
                            "the SILVER to be granted because the IRON promotion\n" +
                            "has not been granted.") {
                        promote "SILVER"
                    }
                }
            }
        }
    }

}
