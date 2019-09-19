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

    @Test
    void 'Global settings'() {
        ontrack.config.sonarQubeSettings = [
                measures: ["measure-1", "measure-2"],
                disabled: false
        ]
        def settings = ontrack.config.sonarQubeSettings
        assert settings.measures == ["measure-1", "measure-2"]
        assert !settings.disabled
    }

}
