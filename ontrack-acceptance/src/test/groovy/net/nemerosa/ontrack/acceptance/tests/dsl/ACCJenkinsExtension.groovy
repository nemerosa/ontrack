package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.http.OTNotFoundException
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test

/**
 * GUI tests about the `jenkins` extension.
 */
@AcceptanceTestSuite
class ACCJenkinsExtension extends AbstractACCDSL {

    /**
     * Testing the obfuscation
     */
    @Test
    void 'Obfuscation of configuration password in properties'() {
        String configurationName = TestUtils.uid('J')
        // Creating the configuration
        ontrack.config.jenkins(configurationName, 'https://jenkins.nemerosa.net', 'user', 'secret')
        // Creates a project and configures it for Jenkins
        String name = TestUtils.uid('P')
        ontrack.project(name).config.jenkinsJob configurationName, 'PRJ'
        // Gets the property
        def jenkins = ontrack.project(name).config.jenkinsJob
        assert jenkins.configuration.name == configurationName
        assert jenkins.configuration.user == 'user'
        assert jenkins.configuration.password == ''
    }
}
