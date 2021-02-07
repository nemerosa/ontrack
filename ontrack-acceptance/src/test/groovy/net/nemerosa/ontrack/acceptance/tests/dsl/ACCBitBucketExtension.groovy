package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.http.OTNotFoundException
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test

/**
 * GUI tests about the `stash` extension (BitBucket).
 */
@AcceptanceTestSuite
class ACCBitBucketExtension extends AbstractACCDSL {

    /**
     * Regression test for #395
     */
    @Test
    void 'Creation and deletion of a configuration'() {
        String configurationName = TestUtils.uid('C') + '.org'
        // Creating the configuration
        ontrack.config.stash configurationName, url: 'https://bitbucket.org'
        // Getting the configuration by name
        def conf = ontrack.get("extension/stash/configurations/${configurationName}")
        assert conf.name == configurationName
        // Deletion
        ontrack.delete("extension/stash/configurations/${configurationName}")
        // Checks it's deleted
        try {
            ontrack.get("extension/stash/configurations/${configurationName}")
            assert false: 'Configuration should have been deleted'
        } catch (OTNotFoundException ignore) {
        }
    }

    /**
     * Testing the obfuscation
     */
    @Test
    void 'Obfuscation of configuration password in properties'() {
        String configurationName = TestUtils.uid('C') + '.org'
        // Creating the configuration
        ontrack.config.stash configurationName, url: 'https://bitbucket.org', user: 'user', password: 'secret'
        // Creates a project and configures it for BitBucket
        String name = TestUtils.uid('P')
        ontrack.project(name).config.stash(configurationName, 'PRJ', 'repos')
        // Gets the property
        def stash = ontrack.project(name).config.stash
        assert stash.configuration.name == configurationName
        assert stash.configuration.user == 'user'
        assert stash.configuration.password == ''
    }
}
