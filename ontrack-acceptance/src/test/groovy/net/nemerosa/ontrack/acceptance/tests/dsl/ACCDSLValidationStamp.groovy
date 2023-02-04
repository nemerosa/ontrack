package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.ValidationRun
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid
import static org.junit.Assert.assertEquals

@AcceptanceTestSuite
class ACCDSLValidationStamp extends AbstractACCDSL {

    @Test
    void 'Validation stamp with spaces'() {
        def projectName = uid("P")
        ontrack.project(projectName) {
            branch("main") {
                def vsName = "Validation stamp with spaces"
                def vs = validationStamp(vsName)
                def fvs = ontrack.validationStamp(projectName, "main", vsName)
                assert fvs.id == vs.id
                assert fvs.name == vsName
            }
        }
    }

}
