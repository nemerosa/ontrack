package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.client.ClientException
import net.nemerosa.ontrack.client.ClientValidationException
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

class ACCDSLPreviousPromotionCheck extends AbstractACCDSL {

    @Test
    void 'Previous promotion required as settings level'() {
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

}
