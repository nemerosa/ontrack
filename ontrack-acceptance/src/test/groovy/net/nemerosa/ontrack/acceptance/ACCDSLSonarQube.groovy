package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCDSLSonarQube extends AbstractACCDSL {

    @Test
    void 'SonarQube configuration'() {
        def name = uid('S')
        ontrack.configure {
            sonarQube name, 'http://sonarqube.nemerosa.net'
        }
        assert ontrack.config.sonarQube.find { it == name } != null
    }

}
